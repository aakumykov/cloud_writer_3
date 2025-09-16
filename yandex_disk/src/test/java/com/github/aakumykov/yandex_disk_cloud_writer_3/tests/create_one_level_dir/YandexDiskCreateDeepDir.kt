package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.takeNthRequest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Test

class YandexDiskCreateDeepDir : YandexCreateDirBase() {

    private val maxDeepDirLevel = 10

    //
    // Проверка запросов
    //
    @Test
    fun create_one_level_deep_dir(): Unit = runBlocking {

        val dirName = randomId

        enqueueResponseCodes(201)

        mockCloudWriter.createDeepDir(listOf(dirName))

        checkRequest(
            HTTP_METHOD_PUT,
            realCloudWriter.apiPathResources,
            YandexDiskCloudWriter.PARAM_PATH to dirName
        )
    }


    @Test
    fun create_multi_level_deep_dir() {
        repeat(maxDeepDirLevel) { i ->
            val n = i+2

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
}
