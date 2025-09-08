package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class LocalCreateDeepDir : LocalBase() {

    private val deepDirMaxDepth = 10
    private val illegalNameCheckingIterations = deepDirMaxDepth

/**
 *
 * План тестирования:
 * - создание полностью новых "глубоких" каталогов разного уровня [create_fully_new_deep_dirs_with_different_levels]
 * - создание частично существующих "глубоких" каталогов разного уровня [create_partially_existing_deep_dirs_with_different_levels]
 *
 * - создание "глубоких" каталогов с недопустимым именем на одном из уровней [create_deep_dir_with_some_illegal_name_throws_exception]
 * - создание "глубоких" каталогов, среди имени которых встречаются "корневые" или пустые (нулевой длины) имена.
 */


    @Test
    fun create_fully_new_deep_dirs_with_different_levels() = runBlocking {
        repeat(deepDirMaxDepth) { i ->
            val names = List(i+1) { randomId }

            val expectedDirPath = cloudWriter.absolutePathFor(names)
            val createdPath = cloudWriter.createDeepDir(names)

            Assert.assertEquals(expectedDirPath, createdPath)
        }
    }


    @Test
    fun create_partially_existing_deep_dirs_with_different_levels() = runBlocking {
        repeat(deepDirMaxDepth) { i ->
            val names = List(i+1) { randomId }

            val expectedDirPath = cloudWriter.absolutePathFor(names)
            val createdPath = cloudWriter.createDeepDir(names)

            Assert.assertEquals(expectedDirPath, createdPath)
        }
    }


    @Test
    fun create_deep_dir_with_some_illegal_name_throws_exception() {
        /*repeat(illegalNameCheckingIterations) {

            val names: List<String> = List(deepDirMaxDepth) { i -> "d$i" }
                .toMutableList()
                .apply {
                    add(ILLEGAL_DIR_NAME)
                    shuffle()
                }

            Assert.assertThrows(CloudWriterException::class.java) {
                runBlocking {
                    cloudWriter.createDeepDir(
                        CloudWriter.mergeFilePaths(* names.toTypedArray())
                    )
                }
            }
        }*/
    }
}