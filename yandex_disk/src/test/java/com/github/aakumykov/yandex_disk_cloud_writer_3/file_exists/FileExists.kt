package com.github.aakumykov.yandex_disk_cloud_writer_3.file_exists

import com.github.aakumykov.yandex_disk_cloud_writer_3.ROOT_PATH
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriterBase
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Test

class FileExists : YandexDiskCloudWriterBase() {

    @Test
    fun root_dir_exists(): Unit = runBlocking {

        mockWebServer.enqueue(MockResponse())

        yandexDiskCloudWriter.fileExists(ROOT_PATH)

        Assert.assertEquals(
            "GET",
            mockWebServer.takeRequest().method
        )
    }
}