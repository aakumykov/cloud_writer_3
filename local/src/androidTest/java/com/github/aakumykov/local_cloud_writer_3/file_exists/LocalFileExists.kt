package com.github.aakumykov.local_cloud_writer_3.file_exists

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
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
 * Б) обычные (относительные) методы с одним аргументом: "полный путь"
 *  - пустой каталог (будет равным виртуальному) [empty_dir_exists_as_it_equals_virtual_root]
 *  - "корневой каталог" относительно виртуального корня [virtual_root_exists]
 *  - существующий (специально созданный) файл [created_dir_exists]
 *  - несуществующий каталог [unexistent_path_does_not_exists]
 *
 * В) обычные ("относительные") методы с двумя аргументами: родитель-потомок
 *  - два пустых [two_empty_args_equals_virtual_root_that_exists]
 *  - непустой и пустой [not_empty_and_empty]
 *  - пустой и непустой [empty_and_not_empty]
 *  - непустой и непустой [both_not_empty]
 *  - многоуровневый непустой и пустой [deep_not_empty_and_empty]
 *  - пустой и многоуровневый непустой [empty_and_deep_not_empty]
 *  - многоуровневый непустой и многоуровневый непустой [both_deep_not_empty]
 */
class LocalFileExists : LocalBase() {

    //
    // Абсолютныя
    //
    @Test
    fun native_linux_root_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(ROOT_DIR)
        )
    }


    @Test
    fun storage_dir_exists() = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(storageRootPath, isAbsolute = true)
        )
    }


    @Test
    fun nonexistent_absolute_path_not_exists() = runBlocking {
        val path = "${ROOT_DIR}${randomId}"
        Assert.assertFalse(
            cloudWriter.fileExists(path)
        )
    }


    //
    // Относительныя с одним аргументом.
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


    //
    // Относительныя с двумя аргументами.
    //
    @Test
    fun two_empty_args_equals_virtual_root_that_exists() = runBlocking {
        Assert.assertTrue(cloudWriter.fileExists(EMPTY_DIR, EMPTY_DIR))
    }


    @Test
    fun not_empty_and_empty() = runBlocking {
        val parentDirName = createDirInStorage(DIR_NAME).name
        Assert.assertTrue(
            cloudWriter.fileExists(parentDirName, EMPTY_DIR)
        )
    }


    @Test
    fun empty_and_not_empty() = runBlocking {
        val childDirName = createDirInStorage(DIR_NAME).name
        Assert.assertTrue(
            cloudWriter.fileExists(EMPTY_DIR, childDirName)
        )
    }


    @Test
    fun both_not_empty() = runBlocking {
        val parentDir = createDirInStorage(DIR_NAME)
        val parentDirName = parentDir.name
        val childDirName = File(parentDir, DIR_NAME).apply { mkdir() }.name
        Assert.assertTrue(
            cloudWriter.fileExists(parentDirName, childDirName)
        )
    }


    @Test
    fun deep_not_empty_and_empty() = runBlocking {
        val parentDirsRelativePath = CloudWriter.mergeFilePaths(DIR_NAME, DIR_NAME)
        createDirInStorage(parentDirsRelativePath)
        Assert.assertTrue(
            cloudWriter.fileExists(parentDirsRelativePath, EMPTY_DIR)
        )
    }


    @Test
    fun empty_and_deep_not_empty() = runBlocking {
        val childDirsRelativePath = CloudWriter.mergeFilePaths(DIR_NAME, DIR_NAME)
        createDirInStorage(childDirsRelativePath)
        Assert.assertTrue(
            cloudWriter.fileExists(EMPTY_DIR, childDirsRelativePath)
        )
    }


    @Test
    fun both_deep_not_empty(): Unit = runBlocking {
        val deepDirsRelativePath = CloudWriter.mergeFilePaths(DIR_NAME, DIR_NAME)
        createDirInStorage(deepDirsRelativePath)
        createDirInStorage(CloudWriter.mergeFilePaths(deepDirsRelativePath,deepDirsRelativePath))
        Assert.assertTrue(
            cloudWriter.fileExists(deepDirsRelativePath, deepDirsRelativePath)
        )
    }


    //
    // Тест вспомогательной внутренней функции.
    //
    @Test
    fun private_fun_create_new_simple_dir_in_storage() = runBlocking {
        Assert.assertTrue(
            createDirInStorage(randomId).exists()
        )
    }

    @Test
    fun private_fun_create_existing_simple_dir_in_storage(): Unit = runBlocking {
        randomId.also { dirName ->
            Assert.assertTrue(File(storageRootPath, dirName).mkdir())
            Assert.assertTrue(createDirInStorage(dirName).exists())
        }
    }


    @Test
    fun private_fun_simple_dir_with_illegal_char_cannot_be_created(): Unit = runBlocking {
        Assert.assertThrows(RuntimeException::class.java) {
            createDirInStorage(ILLEGAL_DIR_NAME)
        }
    }


    @Test
    fun private_fun_create_new_deep_dir_in_storage() = runBlocking {
        Assert.assertTrue(
            createDirInStorage(
                CloudWriter.mergeFilePaths(randomId, randomId)
            ).exists()
        )
    }

    @Test
    fun private_fun_create_existing_deep_dir_in_storage(): Unit = runBlocking {
        CloudWriter.mergeFilePaths(randomId,randomId).also { deepDirName ->
            Assert.assertTrue(File(storageRootPath, deepDirName).mkdirs())
            Assert.assertTrue(createDirInStorage(deepDirName).exists())
        }
    }

    @Test
    fun private_fun_deep_dir_with_illegal_char_cannot_be_created(): Unit = runBlocking {
        Assert.assertThrows(RuntimeException::class.java) {
            createDirInStorage(CloudWriter.mergeFilePaths(randomId, ILLEGAL_DIR_NAME))
        }
    }



    private fun createDirInStorage(dirName: CharSequence): File {

        val createdDir = File(storageRootPath, dirName.toString())

        return if (createdDir.exists()) createdDir
        else {
            if (createdDir.mkdirs()) return createdDir
            else throw RuntimeException("Dir '$dirName' cannot be created")
        }
    }

    companion object {
        private const val DIR_NAME = "some_dir"
        private val ILLEGAL_DIR_NAME = String(charArrayOf(Char(0)))
    }
}