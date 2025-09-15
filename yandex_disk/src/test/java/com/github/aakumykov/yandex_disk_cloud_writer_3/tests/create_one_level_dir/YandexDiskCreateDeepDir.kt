package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_GET
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Test

class YandexDiskCreateDeepDir : YandexCreateDirBase() {

    private val maxDeepDirLevel = 3

    //
    // Проверка запросов
    //
    @Test
    fun create_one_level_deep_dir(): Unit = runBlocking {

        val dirName = randomId

        enqueueResponseCodes(201)

        mockCloudWriter.createDeepDir(listOf(dirName))

        checkRequest(HTTP_METHOD_PUT, dirName)
    }


    @Test
    fun create_multi_level_deep_dir(): Unit {
        /*repeat(maxDeepDirLevel) { i ->
            val dirNames: List<String> = buildList { repeat(i+1) { randomId } }
            repeat(i+1) {
                enqueueResponseCodes(200)
                enqueueResponseCodes(201)
            }
            runBlocking {
                mockCloudWriter.createDeepDir(dirNames)
                checkRequest(HTTP_METHOD_GET, realCloudWriter.absolutePathFor(dirNames))
            }
        }*/

        val i = 1

        val dirNames: List<String> = buildList { repeat(i+1) { add(randomId) } }

        enqueueResponseCodes(200)
        enqueueResponseCodes(201)

        runBlocking {
            mockCloudWriter.createDeepDir(dirNames)
            checkRequest(HTTP_METHOD_GET, realCloudWriter.absolutePathFor(dirNames))
        }
    }
}