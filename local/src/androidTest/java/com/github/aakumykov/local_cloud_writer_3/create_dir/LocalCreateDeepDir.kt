package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class LocalCreateDeepDir : LocalBase() {

    private val deepDirMaxDepth = 10

/**
 *
 * План тестирования:
 *
 * А) Должно работать гладко:
 * 
 * - создание полностью новых "глубоких" каталогов разного уровня
 *  [create_fully_new_deep_dirs_with_different_levels]
 *
 * - создание частично существующих "глубоких" каталогов разного уровня
 *   [create_partially_existing_deep_dirs_with_different_levels]
 *
 *
 * Б) Должно приводить к исключениям:
 *
 * - создание "глубоких" каталогов с недопустимым именем на одном из уровней
 *   [create_deep_dir_with_some_illegal_name_throws_exception]
 *
 * - создание "глубоких" каталогов, среди имени которых встречаются "корневые"
 *   или пустые (нулевой длины) имена, или имена, среди символов которых
 *   присутствуют корневые или нулевые символы [create_deep_dir_with_empty_or_root_intermediate_name]
 */


    @Test
    fun create_fully_new_deep_dirs_with_different_levels() = runBlocking {
        repeat(deepDirMaxDepth) { i ->
            val names = List(i+1) { randomId }

            val expectedDirPath = cloudWriter.absolutePathFor(CloudWriter.mergeFilePaths(* names.toTypedArray()))
            val createdPath = cloudWriter.createDeepDir(names)

            Assert.assertEquals(expectedDirPath, createdPath)
        }
    }


    @Test
    fun create_partially_existing_deep_dirs_with_different_levels() = runBlocking {

        suspend fun createRandomPartialPathOfDeepDir(deepDirPathNames: List<String>) {
            val partialNamesSize = Random.nextInt(1, deepDirPathNames.size)
            val partialNames = deepDirPathNames.subList(0, partialNamesSize).toTypedArray()
            cloudWriter.createDeepDir(partialNames.toList())
        }

        val deepDeerDepthAddition = 2

        repeat(deepDirMaxDepth + deepDeerDepthAddition) { i ->
            if (i >= deepDeerDepthAddition) {

                val deepDirPathNames = List(i + deepDeerDepthAddition) { randomId }

                createRandomPartialPathOfDeepDir(deepDirPathNames)

                val expectedPath = cloudWriter.absolutePathFor(CloudWriter.mergeFilePaths(* deepDirPathNames.toTypedArray()))
                val createdPath = cloudWriter.createDeepDir(deepDirPathNames)

                println("EXPECTED: $expectedPath")
                println("CREATED: $createdPath")
                println("-".repeat(10))

                Assert.assertEquals(
                    expectedPath,
                    createdPath
                )
            }
        }
    }


    @Test
    fun create_deep_dir_with_some_illegal_name_throws_exception() {
        repeat(deepDirMaxDepth) {
            Assert.assertThrows(CloudWriterException::class.java) {
                runBlocking {
                    val dirNames = List(deepDirMaxDepth-1) { randomId }
                        .toMutableList()
                        .apply {
                            add(ILLEGAL_DIR_NAME)
                        }.shuffled()

                    cloudWriter.createDeepDir(dirNames)
                }
            }
        }
    }


    @Test
    fun create_deep_dir_with_empty_or_root_intermediate_name() {
        repeat(deepDirMaxDepth) {
            Assert.assertThrows(IllegalArgumentException::class.java) {
                runBlocking {
                    val dirNames = List(deepDirMaxDepth-2) { randomId }
                        .toMutableList()
                        .apply {
                            add(ROOT_DIR)
                            add(EMPTY_DIR_NAME)
                        }.shuffled()

                    cloudWriter.createDeepDir(dirNames)
                }
            }
        }
    }
}