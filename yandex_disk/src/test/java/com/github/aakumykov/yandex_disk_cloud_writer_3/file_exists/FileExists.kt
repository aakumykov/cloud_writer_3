package com.github.aakumykov.yandex_disk_cloud_writer_3.file_exists

import com.github.aakumykov.yandex_disk_cloud_writer_3.ROOT_PATH
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriterBase
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import org.junit.Test

class FileExists : YandexDiskCloudWriterBase() {

    @Test
    fun root_dir_exists(): Unit = runBlocking {

        yandexDiskCloudWriter.fileExists(ROOT_PATH)

        mockWebServer.takeRequest().also { request: RecordedRequest ->
            Assert.assertEquals("GET", request.method)
            Assert.assertEquals(YandexDiskCloudWriter.YANDEX_API_PATH, request.requestUrl?.encodedPath)
        }
    }

    @Test
    fun unexistent_dir_is_not_exists(): Unit = runBlocking {

    }
}