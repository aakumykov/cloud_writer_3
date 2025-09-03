package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import com.github.aakumykov.local_cloud_writer_3.utils.storageRootPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class LocalCreateDeepDir : LocalBase() {

    private val deepDirMaxDepth = 10

    /**
 * План тестирования:
 * А) "Абсолютное"
 *  - вырожденные случаи:
 *      FIXME: кривая логика то ли метода, то ли принципа тестирования
 *      а) создание вложенных каталогов из "корневых путей" выбрасывает ошибку,
 *         так как будет попытка создать корневой каталог []
 *      б) создание вложенных каталогов из пустых путей
 *      в) смешанный случай.
 *      TODO: нужно ли это вообще тестировать? Ведь это граничные варианты, не
 *      проще ли их фильтровать?
 *
 *  - обычные каталоги:
 *      1) глубокий каталог разной степени глубины [create_deep_dirs_with_different_levels]
 *      2) глубокий каталог где встречаются двойные слеши... (?)
 *      3) глубокий, в котором встречаются запрещённые символы
 *
 *      FIXME: все аргументы-пути
 */

    /*@Test
    fun create_deep_roots_dir() = runBlocking {
        val deepDirPath = CloudWriter.mergeFilePaths(ROOT_DIR, ROOT_DIR)
        val createdDirExpectedPath = CloudWriter.mergeFilePaths(ROOT_DIR, deepDirPath)
        Assert.assertEquals(
            createdDirExpectedPath,
            cloudWriter.createDeepDir(deepDirPath, isAbsolute = true)
        )
    }*/

    @Test
    fun create_deep_dirs_with_different_levels() = runBlocking {
        repeat(deepDirMaxDepth) { i ->
            val p = Array(i+1) { randomId }
            val deepDirPath = CloudWriter.mergeFilePaths(*p)

            // FIXME: нужен какой-то единый метод соединения путей
            val expectedDirPath = CloudWriter.mergeFilePaths(storageRootPath, deepDirPath)
            val createdPath = cloudWriter.createDeepDir(storageRootPath, deepDirPath)

            Assert.assertEquals(
                expectedDirPath,
                createdPath
            )
        }
    }
}