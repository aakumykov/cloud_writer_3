package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
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

        checkRequest(HTTP_METHOD_PUT, YandexDiskCloudWriter.PARAM_PATH to dirName)
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
        val n = i+1

        val dirNames: List<String> = buildList { repeat(n) {
            add(randomId)
            enqueueResponseCodes(201)
        } }

        val deepDirSolidName = CloudWriter.mergeFilePaths(* dirNames.toTypedArray())
        val deepDirPath = realCloudWriter.absolutePathFor(deepDirSolidName)

        runBlocking {
            mockCloudWriter.createDeepDir(dirNames)
            checkRequest(mockWebServer.takeNthRequest(n), HTTP_METHOD_PUT, YandexDiskCloudWriter.PARAM_PATH to deepDirSolidName)
        }
    }
}


@Throws(NoSuchElementException::class)
fun MockWebServer.takeNthRequest(n: Int): RecordedRequest {
    var req: RecordedRequest? = null
    repeat(n) { req = takeRequest() }
    return req!!
}