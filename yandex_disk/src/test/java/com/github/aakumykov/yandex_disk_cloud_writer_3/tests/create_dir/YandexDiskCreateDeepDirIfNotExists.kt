package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.takeNthRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class YandexDiskCreateDeepDirIfNotExists : YandexCreateDirBase() {

    private val maxDeepDirLevel = 10

    //
    // Проверка запросов
    //
    @Test
    fun create_deep_dir_if_not_exists_when_it_fully_not_exists() {

        repeat(maxDeepDirLevel) { i ->

            val depth = i+1
            val dirName = nameForDeepDir(depth)
            val dirSolidName = CloudWriter.mergeFilePaths(dirName)

            //
            // Эти коды ответов нужны YandexCloudWriter-у для корректной работы http-методов.
            // Они не учитываются в последующих проверках.
            //
            // Первая проверка существования глубокого каталога.
            enqueueResponseCodes(404)
            // Создание последнего каталога в цепочке имён глубокого каталога.
            enqueueResponseCodes(201)
            // Последующие создания промежуточных каталогов без проверки.
            repeat(depth) { enqueueResponseCodes(404) }

            runBlocking {
                mockCloudWriter.createDeepDirIfNotExists(dirName)
            }

            checkRequest(
                recordedRequest = mockWebServer.takeNthRequest(depth+1),
                httpMethod = HTTP_METHOD_PUT,
                requestUrl = realCloudWriter.apiPathResources,
                YandexDiskCloudWriter.PARAM_PATH to dirSolidName
            )
        }
    }


    //
    // Проверка возвращаемого значения
    //
    @Test
    fun create_deep_dir_if_not_exists_when_it_partially_exists() {

        repeat(3) { i ->
            val depth = i+2

            val deepDirName = nameForDeepDir(depth)
            val partialDeepDirName = deepDirName.toMutableList().apply {
                repeat(Random.nextInt(1,depth)) {
                    removeLast()
                }
            }

            println("---------------------------------------")
            println("deepDirName       : ${deepDirName.joinToString(" / ")}")
            println("partialDeepDirName: ${partialDeepDirName.joinToString(" / ")}")

            runBlocking {
                Assert.assertEquals(
                    realCloudWriter.absolutePathFor(partialDeepDirName),
                    realCloudWriter.createDeepDir(partialDeepDirName)
                )

                Assert.assertEquals(
                    realCloudWriter.absolutePathFor(deepDirName),
                    realCloudWriter.createDeepDirIfNotExists(deepDirName)
                )
            }
        }
    }
}