package com.github.aakumykov.yandex_disk_cloud_writer_3.file_exists

import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_GET
import com.github.aakumykov.yandex_disk_cloud_writer_3.ROOT_PATH
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriterBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import org.junit.Test

class FileExists : YandexDiskCloudWriterBase() {

    @Test
    fun root_dir_exists_request(): Unit = runBlocking {

        mockCloudWriter.fileExists(ROOT_PATH)

        mockWebServer.takeRequest().also { request: RecordedRequest ->

            Assert.assertEquals(
                HTTP_METHOD_GET,
                request.method
            )

            Assert.assertEquals(
                mockCloudWriter.apiPathResources,
                request.requestUrl?.encodedPath
            )
        }
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