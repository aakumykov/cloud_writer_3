package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.takeNthRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class YandexDiskCreateDeepDir : YandexCreateDirBase() {

    private val maxDeepDirLevel = 10

    //
    // Проверка запросов
    //
    @Test
    fun create_multi_level_deep_dir() {
        repeat(maxDeepDirLevel) { i ->
            val n = i+1

            val dirNames: List<String> = nameForDeepDir(n)

            repeat(n) { enqueueResponseCodes(201) }

            val deepDirSolidName = CloudWriter.mergeFilePaths(* dirNames.toTypedArray())

            runBlocking {
                mockCloudWriter.createDeepDir(dirNames)
            }

            checkRequest(
                recordedRequest = mockWebServer.takeNthRequest(n),
                httpMethod = HTTP_METHOD_PUT,
                requestUrl = realCloudWriter.apiPathResources,
                YandexDiskCloudWriter.PARAM_PATH to deepDirSolidName
            )

        }
    }


    //
    // Проверка результатов вызова
    //
    @Test
    fun create_one_level_deep_dir_result() {
        repeat(maxDeepDirLevel) { i ->
            val n = i+1
            val deepDirName = nameForDeepDir(n)
            runBlocking {
                Assert.assertEquals(
                    realCloudWriter.absolutePathFor(CloudWriter.mergeFilePaths(deepDirName)),
                    realCloudWriter.createDeepDir(deepDirName)
                )
            }
        }
    }


    @Test
    fun create_deep_dir_with_partially_exists_path() {
        repeat(maxDeepDirLevel) { i ->
            val depth = i+2
            val existenceDepth = Random.nextInt(1,depth)

            val deepDirName = nameForDeepDir(depth)
            val existingDirName = deepDirName.toMutableList().subList(0,existenceDepth)

            val expectedExistingDirPath = realCloudWriter.absolutePathFor(existingDirName)
            val expectedDeepDirPath = realCloudWriter.absolutePathFor(deepDirName)

            runBlocking {
                Assert.assertEquals(
                    expectedExistingDirPath,
                    realCloudWriter.createDeepDir(existingDirName)
                )
                Assert.assertEquals(
                    expectedDeepDirPath,
                    realCloudWriter.createDeepDir(deepDirName)
                )
            }
        }
    }


    //
    // Проверка ошибочных вариантов работы
    //
    @Test
    fun throws_exception_if_deep_dir_exists() {
        repeat(maxDeepDirLevel) { i ->
            val depth = i+1
            Assert.assertThrows(CloudWriterException::class.java) {
                runBlocking {
                    nameForDeepDir(depth).also { name ->
                        realCloudWriter.createDeepDir(name)
                        realCloudWriter.createDeepDir(name)
                    }
                }
            }
        }
    }
}
