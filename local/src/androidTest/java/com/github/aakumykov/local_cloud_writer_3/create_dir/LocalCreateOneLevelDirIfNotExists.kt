package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.LocalCloudWriter
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
     *
     * А) обычные случаи
     *  - создание простого несуществующего каталога [create_new_dir_in_virtual_root]
     *  - создание цепочки несуществующих каталогов [create_chain_of_new_dirs]
     *  - создание простого существующего каталога [create_simple_existing_dir]
     *  - создание несуществующего каталога в несуществующем каталоге [create_new_subdir_in_new_dir]
     *  - создание несуществующего каталога в существующем каталоге [create_new_dir_in_existing_dir]
     *
     *  Б) особые случаи
     *  - создание виртуального корня [create_virtual_root_dir]
     *  - создание стандартного каталога [create_standard_dir]
     *
     *  В) вырожденные случаи
     *  - создание истинного корневого каталога [create_native_root_dir]
     *  - недопустимое имя [illegal_name_throws_exception]
     *  - корневое имя [root_dir_name]
     *  - пустое имя [empty_dir_name]
     */

    @Test
    fun create_new_dir_in_virtual_root() = runBlocking {
        val dirName = randomId
        Assert.assertEquals(
            cloudWriter.absolutePathFor(dirName),
            cloudWriter.createOneLevelDirIfNotExists(dirName)
        )
    }

    @Test
    fun create_chain_of_new_dirs() = runBlocking {

        val subdirNames = List(subdirsDepth) { randomId }

        repeat(subdirNames.size-1) { i ->

            val partialSubdirNames = subdirNames.subList(0,i+1).toTypedArray()
            val subdirRelativePath = CloudWriter.mergeFilePaths(*partialSubdirNames)

            Assert.assertEquals(
                cloudWriter.absolutePathFor(subdirRelativePath),
                cloudWriter.createOneLevelDirIfNotExists(subdirRelativePath)
            )
        }
    }

    @Test
    fun create_simple_existing_dir() = runBlocking {
        val dirName = randomId

        cloudWriter.createOneLevelDir(dirName,)

        Assert.assertEquals(
            cloudWriter.absolutePathFor(dirName),
            cloudWriter.createOneLevelDirIfNotExists(dirName)
        )
    }

    @Test
    fun create_new_subdir_in_new_dir() = runBlocking {
        val dirName = randomId
        val subdirName = randomId
        Assert.assertEquals(
            cloudWriter.absolutePathFor(dirName),
            cloudWriter.createOneLevelDirIfNotExists(dirName)
        )
        Assert.assertEquals(
            cloudWriter.absolutePathFor(subdirName),
            cloudWriter.createOneLevelDirIfNotExists(subdirName)
        )
    }


    @Test
    fun create_new_dir_in_existing_dir() = runBlocking {
        listOf(
            android.os.Environment.DIRECTORY_DOWNLOADS,
            android.os.Environment.DIRECTORY_MUSIC,
            android.os.Environment.DIRECTORY_DOCUMENTS
        ).forEach { existingDirName ->
            val newDirName = randomId
            val subdirName = CloudWriter.mergeFilePaths(existingDirName, newDirName)
            Assert.assertEquals(
                cloudWriter.absolutePathFor(subdirName),
                cloudWriter.createOneLevelDirIfNotExists(subdirName)
            )
        }
    }


    @Test
    fun create_virtual_root_dir() = runBlocking {
        val nativeRootWriter = LocalCloudWriter(ROOT_DIR)
        Assert.assertEquals(
            storageRootPath,
            nativeRootWriter.createOneLevelDirIfNotExists(storageRootPath)
        )
    }


    @Test
    fun create_native_root_dir() = runBlocking {
        val nativeRootWriter = LocalCloudWriter(ROOT_DIR)
        Assert.assertEquals(
            ROOT_DIR,
            nativeRootWriter.createOneLevelDirIfNotExists(ROOT_DIR)
        )
    }


    @Test
    fun create_standard_dir() = runBlocking {
        listOf(
            android.os.Environment.DIRECTORY_DOWNLOADS,
            android.os.Environment.DIRECTORY_MUSIC,
            android.os.Environment.DIRECTORY_DOCUMENTS
        ).forEach { dirName ->
            Assert.assertEquals(
                cloudWriter.absolutePathFor(dirName),
                cloudWriter.createOneLevelDirIfNotExists(dirName)
            )
        }
    }


    // TODO: переделать
    /*@Test
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
    }*/

    
    /*@Test
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
    }*/


    @Test
    fun illegal_name_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDirIfNotExists(ILLEGAL_DIR_NAME)
            }
        }
    }

    @Test
    fun root_dir_name() = runBlocking {
        Assert.assertEquals(
            cloudWriter.absolutePathFor(ROOT_DIR),
            cloudWriter.createOneLevelDirIfNotExists(ROOT_DIR)
        )
    }

    @Test
    fun empty_dir_name() = runBlocking {
        Assert.assertEquals(
            cloudWriter.absolutePathFor(EMPTY_DIR_NAME),
            cloudWriter.createOneLevelDirIfNotExists(EMPTY_DIR_NAME)
        )
    }
}