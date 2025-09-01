package com.github.aakumykov.local_cloud_writer_3.file_exists

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.local_cloud_writer_3.base.StorageAccessTestCase
import com.github.aakumykov.local_cloud_writer_3.utils.localCloudWriter
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import com.github.aakumykov.local_cloud_writer_3.utils.storageRootPath
import com.github.aakumykov.local_cloud_writer_3.utils.tempFile
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * План тестирования:
 *
 * А) абсолютные методы
 *  - абсолютный корневой каталог (/) [native_linux_root_dir_exists]
 *  - абсолютный каталог, играющий роль виртуального корня [storage_dir_exists]
 *  - несуществующий путь [nonexistent_absolute_path_not_exists]
 *
 * Б) относительные методы
 *  - пустой каталог (будет равным виртуальному) [empty_dir_exists_as_it_equals_virtual_root]
 *  - "корневой каталог" относительно виртуального корня [virtual_root_exists]
 *  - существующий (специально созданный) файл [created_dir_exists]
 *  - несуществующий каталог [unexistent_path_does_not_exists]
 */
class LocalFileExists : StorageAccessTestCase() {

    private val cloudWriter: CloudWriter by lazy {
        localCloudWriter(storageRootPath)
    }

    companion object {
        const val ROOT_DIR = "/"
        const val EMPTY_DIR = ""
    }


    //
    // Абсолютныя
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
        val path = "${ROOT_DIR}${randomId}"
        Assert.assertFalse(
            cloudWriter.fileExists(path, true)
        )
    }


    //
    // Относительныя
    //
    @Test
    fun empty_dir_exists_as_it_equals_virtual_root() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(EMPTY_DIR, false)
        )
    }


    @Test
    fun virtual_root_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(ROOT_DIR, false)
        )
    }


    @Test
    fun created_dir_exists(): Unit = runBlocking {

        val dirName = randomId
        File(storageRootPath, dirName).apply { mkdir() }

        tempFile.also {
            Assert.assertTrue(
                cloudWriter.fileExists(dirName, false)
            )
        }
    }


    @Test
    fun unexistent_path_does_not_exists() = runBlocking {
        Assert.assertFalse(
            cloudWriter.fileExists(randomId, false)
        )
    }

}