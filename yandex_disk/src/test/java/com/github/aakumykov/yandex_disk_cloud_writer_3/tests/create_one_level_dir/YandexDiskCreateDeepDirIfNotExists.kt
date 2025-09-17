package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.takeNthRequest
import kotlinx.coroutines.runBlocking
import org.junit.Test

class YandexDiskCreateDeepDirIfNotExists : YandexCreateDirBase() {

    private val maxDeepDirLevel = 10

    //
    // Проверка запросов
    //
    @Test
    fun create_multi_level_deep_dir_if_not_exists() {
        repeat(maxDeepDirLevel) { i ->
            val depth = i+1
            val dirName = nameForDeepDir(depth)
            val dirSolidName = CloudWriter.mergeFilePaths(dirName)
            repeat(depth) {
                enqueueResponseCodes(404)
                enqueueResponseCodes(201)
            }
            runBlocking {
                mockCloudWriter.createDeepDirIfNotExists(dirName)
            }
            checkRequest(
                recordedRequest = mockWebServer.takeNthRequest(depth),
                httpMethod = HTTP_METHOD_PUT,
                requestUrl = realCloudWriter.apiPathResources,
                YandexDiskCloudWriter.PARAM_PATH to dirSolidName
            )
        }
    }
}