package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.downloadsDirPath
import com.github.aakumykov.local_cloud_writer_3.utils.musicDirPath
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import com.github.aakumykov.local_cloud_writer_3.utils.storageRootPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class LocalCreateOneLevelDir : LocalBase() {

/**
 * План тестирования:
 *
 * Каталоги всегда создаются относительно виртуального корня.
 * Поэтому, чтобы создать каталог с использованием абсолютного пути,
 * нужно установить виртуальный корень в "/". На текущий момент
 * это означает создание нового экземпляра [CloudWriter].
 *
 * - истинный корневой каталог не создаётся, так как уже существует [native_root_dir_throws_exception]
 * - виртуальный корень [storageRootPath] не создаётся [virtual_root_dir_throws_exception]
 * - простой каталог создаётся [simple_dir_is_created]
 * - вложенный каталог не создаётся, так как дерево не создать одним вызовом [deep_dir_is_not_created]
 * - вложенный каталог создаётся поэтапно вглубь [deep_dir_is_created_step_by_step_in_deep]
 *
 */

    @Test
    fun native_root_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(ROOT_DIR)
            }
        }
    }


    @Test
    fun virtual_root_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(storageRootPath)
            }
        }
    }


    @Test
    fun simple_dir_is_created() = runBlocking {
        val dirName = randomId
        val expectedDirPath = cloudWriter.absolutePathFor(dirName)
        Assert.assertEquals(
            expectedDirPath,
            cloudWriter.createOneLevelDir(dirName)
        )
    }


    @Test
    fun deep_dir_is_not_created() {
        val parentDirName = randomId
        val childDirName = randomId
        val deepDirName = CloudWriter.mergeFilePaths(parentDirName, childDirName)
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(deepDirName)
            }
        }
    }


    @Test
    fun deep_dir_is_created_step_by_step_in_deep() = runBlocking {
        val parentDirName = randomId
        val childDirName = randomId

        val deepDirName = CloudWriter.mergeFilePaths(parentDirName, childDirName)
        val expectedDirPath = cloudWriter.absolutePathFor(deepDirName)

        cloudWriter.createOneLevelDir(parentDirName)

        Assert.assertEquals(
            expectedDirPath,
            cloudWriter.createOneLevelDir(parentDirName, childDirName)
        )
    }

}