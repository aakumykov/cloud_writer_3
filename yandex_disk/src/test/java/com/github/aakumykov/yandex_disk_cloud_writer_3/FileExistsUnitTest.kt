package com.github.aakumykov.yandex_disk_cloud_writer_3

import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.yandexDiskCloudWriter
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class FileExistsUnitTest {

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