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
 * А) абсолютный:
 *  - истинный корневой каталог не создаётся [native_root_dir_throws_exception]
 *  - виртуальный корневой каталог (storage) не создаётся [virtual_root_dir_throws_exception]
 *  - подкаталог виртуального корня создаётся [virtual_root_subdir_created]
 *  - подкаталоги каталога "Загрузки" создаются [downloads_dir_subdir_created], [music_dir_subdir_created]
 *  - создание многоуровневого каталога вызывает исключение [multi_level_dir_throws_exception]
 *
 *  Метод-обёртку с двумя аргументами можно не проверять, так как внутри он
 *  использует протестированный метод [CloudWriter.mergeFilePaths].
 *  А вот и наоборот! Внутри обёртки я упустил передачу аргумента "isAbsolute",
 *  и это осталось непротестированным :-(
 *
 * Б) относительный:
 *  - корневой (/) не создаётся, так как уже существует [relative_root_dir_throws_exception]
 *  - пустой не создаётся, так как совпадает с корнем [relative_empty_dir_throws_exception]
 *  - одноуровневый каталог создаётся [relative_one_level_dir_created]
 *  - стандартный каталог не создаётся, так как уже существует [relative_standard_dir_throws_exception]
 *  - каталог с недопустимым (нулевым) символом вызывает исключение [relative_illegal_name_dir_throws_exception]
 *  - многоуровневый каталог бросает исключение [relative_multi_level_dir_throws_exception]
 */

    @Test
    fun native_root_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(ROOT_DIR, isAbsolute = true)
            }
        }
    }


    @Test
    fun virtual_root_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(storageRootPath, isAbsolute = true)
            }
        }
    }


    @Test
    fun virtual_root_subdir_created(): Unit = runBlocking {
        val dirPath = CloudWriter.mergeFilePaths(storageRootPath, randomId)
        Assert.assertEquals(
            dirPath,
            cloudWriter.createOneLevelDir(dirPath, isAbsolute = true)
        )
    }


    @Test
    fun downloads_dir_subdir_created(): Unit = runBlocking {
        val dirAbsolutePath = CloudWriter.mergeFilePaths(downloadsDirPath, randomId)
        Assert.assertEquals(
            dirAbsolutePath,
            cloudWriter.createOneLevelDir(dirAbsolutePath, isAbsolute = true)
        )
    }


    @Test
    fun music_dir_subdir_created(): Unit = runBlocking {
        val dirAbsolutePath = CloudWriter.mergeFilePaths(musicDirPath, randomId)
        Assert.assertEquals(
            dirAbsolutePath,
            cloudWriter.createOneLevelDir(dirAbsolutePath, isAbsolute = true)
        )
    }


    @Test
    fun multi_level_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            val dirName = CloudWriter.mergeFilePaths(randomId, randomId)
            runBlocking {
                cloudWriter.createOneLevelDir(
                    dirPath = CloudWriter.mergeFilePaths(storageRootPath, dirName),
                    isAbsolute = true
                )
            }
        }
    }


    //
    // Относительныя методы
    //

    @Test
    fun relative_root_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(ROOT_DIR)
            }
        }
    }


    @Test
    fun relative_empty_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(EMPTY_DIR)
            }
        }
    }


    @Test
    fun relative_one_level_dir_created() = runBlocking {
        val dirName = randomId
        Assert.assertEquals(
            CloudWriter.mergeFilePaths(storageRootPath, dirName),
            cloudWriter.createOneLevelDir(dirName)
        )
    }


    @Test
    fun relative_standard_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(android.os.Environment.DIRECTORY_DOWNLOADS)
            }
        }
    }


    @Test
    fun relative_illegal_name_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(ILLEGAL_DIR_NAME)
            }
        }
    }


    @Test
    fun relative_multi_level_dir_throws_exception() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.createOneLevelDir(CloudWriter.mergeFilePaths(randomId, randomId))
            }
        }
    }
}