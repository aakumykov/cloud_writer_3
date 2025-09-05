package com.github.aakumykov.local_cloud_writer_3.create_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class LocalCreateDeepDir : LocalBase() {

    private val deepDirMaxDepth = 10
    private val illegalNameCheckingIterations = deepDirMaxDepth

/**
 * План тестирования:
 * - создание "глубоких" каталогов разного уровня [create_deep_dirs_with_different_levels]
 *
 * - создание таких каталогов с недопустимым именем на одном из уровней [create_deep_dir_with_some_illegal_name_throws_exception]
 * - создание с промежуточными пустыми именами [create_deep_dir_with_some_empty_names_]
 * - создание с промежуточными "корневыми" именами []
 *
 * - создание с пустыми именами []
 * - создание с "корневыми" именами []
 * - сочетание пустых и "корневых" имён []
 */


    @Test
    fun create_deep_dirs_with_different_levels() = runBlocking {
        repeat(deepDirMaxDepth) { i ->
            val names = Array(i+1) { randomId }
            val deepDirPath = CloudWriter.mergeFilePaths(*names)

            val expectedDirPath = cloudWriter.absolutePathFor(deepDirPath)
            val createdPath = cloudWriter.createDeepDir(deepDirPath)

            Assert.assertEquals(
                expectedDirPath,
                createdPath
            )
        }
    }


    @Test
    fun create_deep_dir_with_some_illegal_name_throws_exception() {
        repeat(illegalNameCheckingIterations) {

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
        }
    }
}