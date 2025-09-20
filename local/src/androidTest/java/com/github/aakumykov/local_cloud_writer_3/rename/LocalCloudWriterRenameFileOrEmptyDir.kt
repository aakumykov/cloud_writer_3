package com.github.aakumykov.local_cloud_writer_3.rename

import androidx.core.util.Supplier
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.base.LocalFileCreationBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.io.File

class LocalCloudWriterRenameFileOrEmptyDir : LocalFileCreationBase() {

    @Test
    fun renaming_simple_file() = runBlocking {
        renameOldToNew(
            { aFile.apply { createNewFile() } },
            { aFile }
        )
    }

    @Test
    fun renaming_empty_dir() = runBlocking {
        renameOldToNew(
            { aFile.apply { mkdir() } },
            { aFile }
        )
    }

    @Test
    fun return_false_on_renaming_unexistent_file() = runBlocking {
        listOf(true, false).forEach { doOverwrite ->
            Assert.assertFalse(
                cloudWriter.renameFileOrEmptyDir(randomId, randomId, doOverwrite)
            )
        }
    }

    private fun renameOldToNew(old: Supplier<File>, new: Supplier<File>) = runBlocking {

        listOf(true, false).forEach { doOverwrite ->

            val oldFile = old.get()
            val newFile = new.get()

            Assert.assertTrue(oldFile.exists())
            Assert.assertFalse(newFile.exists())

            Assert.assertTrue(
                cloudWriter.renameFileOrEmptyDir(
                    oldFile.name,
                    newFile.name,
                    doOverwrite
                )
            )

            Assert.assertFalse(oldFile.exists())
            Assert.assertTrue(newFile.exists())
        }
    }

    private val aFile: File get() = File(cloudWriter.absolutePathFor(randomId))
}