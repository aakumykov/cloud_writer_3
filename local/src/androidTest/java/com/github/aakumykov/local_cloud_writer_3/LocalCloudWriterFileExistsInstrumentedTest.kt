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

    /**
     * План тестирования:
     * А) абсолютные методы
     *  - абсолютный корневой каталог (/);
     *  - виртуальный корневой каталог ("storage");
     *  - несуществующий путь не существует;
     * Б) относительные методы
     *  - пустой каталог (будет равным виртуальному);
     *  - "корневой каталог" относительно виртуального корня;
     *  - несуществующий каталог;
     *  - существующий (специально созданный) файл;
     */

    private val cloudWriter: CloudWriter by lazy {
        localCloudWriter(storageRootPath)
    }

    //
    // "Абсолютные" методы.
    //

    @Test
    fun native_linux_root_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(ROOT_DIR, true)
        )
    }


    @Test
    fun storage_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(storageRootPath, true)
        )
    }


    @Test
    fun nonexistent_absolute_path_not_exists() = runBlocking {
        Assert.assertFalse(
            cloudWriter.fileExists(randomId, true)
        )
    }


    //
    // Относительные методы
    //

    /**
     * Проверяет наличие виртуального корневого каталога
     * по отношению к виртуальному корню.
     */



    /**
     * Проверяет наличие истинного корневого каталога.
     */
    /*@Test
    fun true_root_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(ROOT_DIR, false)
        )
    }


    *//**
     * Отсутствующий файл должен сообщаться как отсутствующий.
     *//*
    @Test
    fun unexistent_relative_path_does_not_exists() = runBlocking {
        Assert.assertFalse(
            cloudWriter.fileExists(
                randomId,
                true
            )
        )
    }


    *//**
     * Существующий файл должен сообщаться как таковой.
     *//*
    @Test
    fun temp_file_exists() {
        tempFile.also {
            Assert.assertTrue(tempFile.exists())
        }
    }


    *//**
     * Существующий каталог должен сообщаться как таковой.
     *//*
    @Test
    fun temp_dir_exists() = runBlocking {
        File(tempFile, randomId).apply {
            mkdirs()
            Assert.assertTrue(this.exists())
        }
    }*/


    companion object {
        const val ROOT_DIR = "/"
    }
}