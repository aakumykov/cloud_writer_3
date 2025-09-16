package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase.Companion.ILLEGAL_DIR_NAME
import com.github.aakumykov.cloud_writer_3.utils_for_tests.RingList
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
     * - простой каталог создаётся [simple_dir_is_created]
     * - проверка действия аргумента "игнорировать, если существует" [ignore_already_exists_argument]
     *
     * - вложенный каталог не создаётся, так как дерево не создать одним вызовом [deep_dir_is_not_created]
     * - вложенный каталог создаётся поэтапно вглубь [deep_dir_is_created_step_by_step_in_deep]
     *
     * - особые случаи:
     *   а) каталоги с именами [ROOT_DIR], [EMPTY_DIR_NAME], [ILLEGAL_DIR_NAME]
     * вызывают исключения [illegal_dir_names_are_not_created]
     *   б) разные попарные сочетания [ROOT_DIR], [EMPTY_DIR_NAME], [ILLEGAL_DIR_NAME]
     * вызывают исключения [bad_dir_names_pairs_are_not_created]
     */

    @Test
    fun simple_dir_is_created() = runBlocking {
        val dirName = randomId
        val expectedDirPath = cloudWriter.absolutePathFor(dirName)
        Assert.assertEquals(
            expectedDirPath,
            cloudWriter.createOneLevelDir(dirName,)
        )
    }

    @Test
    fun ignore_already_exists_argument(): Unit = runBlocking {
        val dirName = randomId
        cloudWriter.createOneLevelDir(dirName)

        val expectedDirPath = cloudWriter.absolutePathFor(dirName)
        Assert.assertEquals(
            expectedDirPath,
            cloudWriter.createOneLevelDir(dirName, true)
        )

        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(dirName, false)
            }
        }
    }


    @Test
    fun deep_dir_is_not_created() {
        val parentDirName = randomId
        val childDirName = randomId
        val deepDirName = CloudWriter.mergeFilePaths(parentDirName, childDirName)
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(deepDirName,)
            }
        }
    }


    @Test
    fun deep_dir_is_created_step_by_step_in_deep() = runBlocking {
        val parentDirName = randomId
        val childDirName = randomId

        val deepDirName = CloudWriter.mergeFilePaths(parentDirName, childDirName)
        val expectedDirPath = cloudWriter.absolutePathFor(deepDirName)

        cloudWriter.createOneLevelDir(parentDirName,)

        Assert.assertEquals(
            expectedDirPath,
            cloudWriter.createOneLevelDir(parentDirName, childDirName)
        )
    }


    @Test
    fun virtual_root_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(storageRootPath,)
            }
        }
    }


    @Test
    fun illegal_dir_names_are_not_created() {
        listOf(ROOT_DIR, EMPTY_DIR_NAME, ILLEGAL_DIR_NAME).forEach { dirName ->
            Assert.assertThrows(CloudWriterException::class.java) {
                runBlocking {
                    cloudWriter.createOneLevelDir(dirName,)
                }
            }
        }
    }


    @Test
    fun bad_dir_names_pairs_are_not_created() {

        val illegalNamesRing = RingList(listOf(
            ROOT_DIR,
            EMPTY_DIR_NAME,
            ILLEGAL_DIR_NAME
        ))

        for(i in 1..3) {
            val parentDirName = illegalNamesRing.get(i)
            val childDirName = illegalNamesRing.get(i+1)

            "${i}--${i+1}".also { println(it) }

            Assert.assertThrows(CloudWriterException::class.java) {
                runBlocking {
                    cloudWriter.createOneLevelDir(parentDirName, childDirName)
                }
            }
        }
    }
}