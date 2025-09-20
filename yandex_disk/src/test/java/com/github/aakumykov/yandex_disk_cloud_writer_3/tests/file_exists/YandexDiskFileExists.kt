package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.file_exists

import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_GET
import com.github.aakumykov.yandex_disk_cloud_writer_3.ROOT_PATH
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import org.junit.Test

class YandexDiskFileExists : YandexDiskBase() {

    @Test
    fun root_dir_exists_request(): Unit = runBlocking {

        enqueueResponseCodes(200)

        mockCloudWriter.fileExists(ROOT_PATH)

        checkRequest(
            recordedRequest = mockWebServer.takeRequest(),
            httpMethod = HTTP_METHOD_GET,
            requestUrlPath = mockCloudWriter.apiPathResources
        )
    }

    @Test
    fun root_dir_exists_response(): Unit = runBlocking {
        Assert.assertTrue(
            realCloudWriter.fileExists(ROOT_PATH)
        )
    }

    @Test
    fun random_dir_exists_response(): Unit = runBlocking {
        Assert.assertFalse(
            realCloudWriter.fileExists(randomId)
        )
    }
}