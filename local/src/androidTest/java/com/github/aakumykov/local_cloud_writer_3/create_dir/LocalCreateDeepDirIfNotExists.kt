package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.local_cloud_writer_3.LocalCloudWriter
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class LocalCreateDeepDirIfNotExists : LocalBase() {

    /**
     * План тестирования:
     *
     * А) Штатная работа:

     *  I) Обычные случаи:
     *  - создание нового глубокого каталога [create_new_deep_dir]
     *  - попытка создания существующего глубокого каталога [create_existing_deep_dir]
     *  - создание частично существующего глубокого каталога [create_partially_existing_deep_dir]
     *
     *  - попытка создания нового одноуровневого каталога [create_new_one_level_dir]
     *  - попытка создания существующего одноуровневого каталога [create_existing_one_level_dir]
     *
     *  II) Краевые случаи:
     *  - попытка создания истинного корневого каталога [create_native_root_dir]
     *  - попытка создания виртуального корневого каталога [create_virtual_root_dir]
     *  - создание каталога с нулевыми символами именами [create_deep_dir_with_illegal_dir_name]
     */

    private val deepDirMaxLength = 10

    private fun nameForDeepDir(depth: Int): List<String> = buildList {
        repeat(depth) {
            add(randomId)
        }
    }

    private fun nameForDeepDirWithIllegalPartInRandomPlace(len: Int, illegalDirName: String): List<String> {
        return nameForDeepDir(len)
            .toMutableList()
            .apply { add(illegalDirName) }
            .shuffled()
    }


    @Test
    fun create_new_deep_dir(): Unit = runBlocking {
        repeat(deepDirMaxLength) { i ->
            val n = i+1
            val names = nameForDeepDir(n)
            println(names)
            Assert.assertEquals(
                cloudWriter.absolutePathFor(names),
                cloudWriter.createDeepDirIfNotExists(names)
            )
        }
    }

    @Test
    fun create_existing_deep_dir(): Unit = runBlocking {
        repeat(deepDirMaxLength) { i ->
            val n = i+1
            val names = nameForDeepDir(n)
            cloudWriter.createDeepDir(names)
            Assert.assertEquals(
                cloudWriter.absolutePathFor(names),
                cloudWriter.createDeepDirIfNotExists(names)
            )
        }
    }


    @Test
    fun create_partially_existing_deep_dir() = runBlocking {
        repeat(deepDirMaxLength) { i ->
            val n = i+2

            val names = nameForDeepDir(n)
            val partialNames = names.toMutableList().apply { removeLast() }

            Assert.assertEquals(
                cloudWriter.absolutePathFor(partialNames),
                cloudWriter.createDeepDirIfNotExists(partialNames)
            )

            Assert.assertEquals(
                cloudWriter.absolutePathFor(names),
                cloudWriter.createDeepDirIfNotExists(names)
            )
        }
    }


    @Test
    fun create_new_one_level_dir() = runBlocking {
        val name = randomId
        Assert.assertEquals(
            cloudWriter.absolutePathFor(name),
            cloudWriter.createDeepDirIfNotExists(listOf(name))
        )
    }


    @Test
    fun create_existing_one_level_dir() = runBlocking {
        val name = randomId
        Assert.assertEquals(
            cloudWriter.absolutePathFor(name),
            cloudWriter.createOneLevelDir(name)
        )
        Assert.assertEquals(
            cloudWriter.absolutePathFor(name),
            cloudWriter.createDeepDirIfNotExists(listOf(name))
        )
    }


    @Test
    fun create_virtual_root_dir() = runBlocking {

        val nativeRootCloudWriter = LocalCloudWriter(ROOT_DIR)
        val virtualRootPath = cloudWriter.virtualRootPath

        Assert.assertEquals(
            virtualRootPath,
            nativeRootCloudWriter.createDeepDirIfNotExists(listOf(virtualRootPath))
        )
    }


    @Test
    fun create_deep_dir_with_illegal_dir_name() {
        listOf(
            ILLEGAL_DIR_NAME,
            EMPTY_DIR_NAME,
            ROOT_DIR
        ).forEach { badName ->
            repeat(deepDirMaxLength) { len ->
                val deepDirName = nameForDeepDirWithIllegalPartInRandomPlace(len, badName)
                Assert.assertThrows(IllegalArgumentException::class.java) {
                    runBlocking {
                        cloudWriter.createDeepDirIfNotExists(deepDirName)
                    }
                }
            }
        }
    }
}