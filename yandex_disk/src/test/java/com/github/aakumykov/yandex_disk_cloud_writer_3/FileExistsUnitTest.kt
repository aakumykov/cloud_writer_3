package com.github.aakumykov.yandex_disk_cloud_writer_3

import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.authToken
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.okHttpClientBuilder
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.yandexDiskCloudWriter
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class FileExistsUnitTest {

    private val server by lazy { MockWebServer() }

    private val yandexDiskCloudWriter = YandexDiskCloudWriter(
        serverUrl = server.url(YandexDiskCloudWriter.YANDEX_API_PATH).toString(),
        authToken = authToken,
        yandexDiskClientCreator = YandexDiskOkHttpClientCreator(okHttpClientBuilder)
    )

    @Test
    fun root_dir_exists(): Unit = runBlocking {
        Assert.assertTrue(
            yandexDiskCloudWriter.fileExists(ROOT_DIR)
        )
    }

    @Test
    fun unique_path_does_not_exists(): Unit = runBlocking {
        Assert.assertFalse(
            yandexDiskCloudWriter.fileExists(
                uniquePath
            )
        )
    }

    companion object {
        const val ROOT_DIR = "/"
        val uniquePath: String get() = UUID.randomUUID().toString()
    }
}