package com.github.aakumykov.local_cloud_writer_3

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.local_cloud_writer_3.base.StorageAccessTestCase
import com.github.aakumykov.local_cloud_writer_3.utils.localCloudWriter
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import com.github.aakumykov.local_cloud_writer_3.utils.storageRootPath
import com.github.aakumykov.local_cloud_writer_3.utils.tempFile
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class LocalCloudWriterFileExistsInstrumentedTest : StorageAccessTestCase() {

    private val cloudWriter: CloudWriter by lazy {
        localCloudWriter(storageRootPath)
    }


    /**
     * Проверяет наличие каталога по отношению к виртуальному корню.
     */
    @Test
    fun relative_root_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(ROOT_DIR, true)
        )
    }


    /**
     * Проверяет наличие самого виртуального корня.
     */
    @Test
    fun absolute_root_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists("", false)
        )
    }


    /**
     * Проверяет наличие истинного корневого каталога.
     */
    @Test
    fun true_root_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(ROOT_DIR, false)
        )
    }


    /**
     * Отсутствующий файл должен сообщаться как отсутствующий.
     */
    @Test
    fun unexistent_relative_path_does_not_exists() = runBlocking {
        Assert.assertFalse(
            cloudWriter.fileExists(
                randomId,
                true
            )
        )
    }


    /**
     * Существующий файл должен сообщаться как таковой.
     */
    @Test
    fun temp_file_exists() {
        tempFile.also {
            Assert.assertTrue(tempFile.exists())
        }
    }


    /**
     * Существующий каталог должен сообщаться как таковой.
     */
    @Test
    fun temp_dir_exists() = runBlocking {
        File(tempFile, randomId).apply {
            mkdirs()
            Assert.assertTrue(this.exists())
        }
    }


    companion object {
        const val ROOT_DIR = "/"
    }
}