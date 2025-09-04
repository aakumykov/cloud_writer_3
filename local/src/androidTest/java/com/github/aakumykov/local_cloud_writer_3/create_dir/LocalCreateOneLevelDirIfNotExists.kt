package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import com.github.aakumykov.local_cloud_writer_3.utils.storageRootPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class LocalCreateOneLevelDirIfNotExists : LocalBase() {

    private val subdirsDepth = 5
    
/**
 * План тестирования:
 * А) "Абсолютное"
 *  - создание истинного корневого каталога ("/") [create_native_root_dir]
 *  - создание виртуального корневого каталога [create_virtual_root_dir]
 *  - создание подкаталога виртуального корневого каталога [create_one_level_dir_in_virtual_root]
 *  - создание подкаталога только что созданного подкаталога [create_self_subdirs_in_virtual_root]
 *  - недопустимый символ в имени приводит к исключению [create_with_illegal_name_throws_exception]
 *  Б) "Относительное"
 *  - создание корневого каталога (равносильно созданию виртуального корня) [relative_create_root_dir]
 *  - создание пустого каталога (равносильно созданию виртуального корня) [relative_create_empty_dir]
 *  - создание обычного каталога [relative_create_one_dir]
 *  - создание обычного каталога в только что созданном обычном каталоге [relative_create_self_subdirs]
 *  - недопустимый символ в имени приводит к исключению [relative_create_illegal_name_throws_exception]
 */

    @Test
    fun create_native_root_dir() = runBlocking {
        Assert.assertEquals(
            ROOT_DIR,
            cloudWriter.createOneLevelDirIfNotExists(ROOT_DIR)
        )
    }

    @Test
    fun create_virtual_root_dir() = runBlocking {
        Assert.assertEquals(
            storageRootPath,
            cloudWriter.createOneLevelDirIfNotExists(storageRootPath)
        )
    }

    @Test
    fun create_one_level_dir_in_virtual_root() = runBlocking {
        val dirPath = CloudWriter.mergeFilePaths(storageRootPath, randomId)
        Assert.assertEquals(
            dirPath,
            cloudWriter.createOneLevelDirIfNotExists(dirPath)
        )
    }

    @Test
    fun create_self_subdirs_in_virtual_root() = runBlocking {
        val subdirNames = List(subdirsDepth) { randomId }
        repeat(subdirNames.size) { i ->
            val partialSubdirNames = subdirNames.subList(0,i).toTypedArray()
            val subdirRelativePath = CloudWriter.mergeFilePaths(*partialSubdirNames)
            val dirPath = CloudWriter.mergeFilePaths(storageRootPath, subdirRelativePath)
            Assert.assertEquals(
                dirPath,
                cloudWriter.createOneLevelDirIfNotExists(dirPath)
            )
        }
    }


    @Test
    fun create_with_illegal_name_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDirIfNotExists(
                    ILLEGAL_DIR_NAME
                )
            }
        }
    }
    
    
    //
    // Относительные
    //
    @Test
    fun relative_create_root_dir() = runBlocking { 
        Assert.assertEquals(
            CloudWriter.mergeFilePaths(storageRootPath, ROOT_DIR),
            cloudWriter.createOneLevelDirIfNotExists(ROOT_DIR)
        )
    }
    
    @Test
    fun relative_create_empty_dir() = runBlocking {
        Assert.assertEquals(
            CloudWriter.mergeFilePaths(storageRootPath, EMPTY_DIR_NAME),
            cloudWriter.createOneLevelDirIfNotExists(EMPTY_DIR_NAME)
        )
    }
    
    @Test
    fun relative_create_one_dir() = runBlocking {
        val dirName = randomId
        Assert.assertEquals(
            CloudWriter.mergeFilePaths(storageRootPath, dirName),
            cloudWriter.createOneLevelDirIfNotExists(dirName)
        )
    }
    
    @Test
    fun relative_create_self_subdirs() = runBlocking {
        val subdirNames = List(subdirsDepth) { randomId }
        repeat(subdirNames.size) { i ->
            val partialSubdirNames = subdirNames.subList(0,i).toTypedArray()
            val subdirRelativePath = CloudWriter.mergeFilePaths(*partialSubdirNames)
            val dirPath = CloudWriter.mergeFilePaths(storageRootPath, subdirRelativePath)
            Assert.assertEquals(
                dirPath,
                cloudWriter.createOneLevelDirIfNotExists(subdirRelativePath)
            )
        }
    }

    @Test
    fun relative_create_illegal_name_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDirIfNotExists(
                    ILLEGAL_DIR_NAME
                )
            }
        }
    }
}