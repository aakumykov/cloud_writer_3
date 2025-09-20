package com.github.aakumykov.local_cloud_writer_3.delete

import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.local_cloud_writer_3.base.LocalBase
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.io.File

class LocalCloudWriterDeleteFileOrEmptyDir : LocalBase() {

    @Test
    fun deleting_a_file(): Unit = runBlocking{
        val fileName = randomId
        val file = newFile(fileName)
        Assert.assertTrue(file.exists())
        Assert.assertEquals(
            cloudWriter.virtualRootPlus(fileName),
            cloudWriter.deleteFileOrEmptyDir(fileName))
        Assert.assertFalse(file.exists())
    }


    @Test
    fun deleting_empty_dir(): Unit = runBlocking {
        val dirName = randomId
        val dir = File(cloudWriter.virtualRootPlus(dirName)).apply { mkdir() }

        Assert.assertTrue(dir.exists())

        Assert.assertEquals(
            cloudWriter.virtualRootPlus(dirName),
            cloudWriter.deleteFileOrEmptyDir(dirName))

        Assert.assertFalse(dir.exists())
    }

    @Test
    fun throws_exception_deleting_unexistent_file() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.deleteFileOrEmptyDir(randomId)
            }
        }
    }

    @Test
    fun throws_exception_deleting_non_empty_dir() {

        val dir1Name = randomId
        val dir2Name = randomId

        val dir1 = File(cloudWriter.absolutePathFor(dir1Name))
        val dir2 = File(dir1, dir2Name)

        Assert.assertTrue(dir1.mkdir())
        Assert.assertTrue(dir2.mkdir())

        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                cloudWriter.deleteFileOrEmptyDir(dir1Name)
            }
        }
    }


    private fun newFile(name: String): File {
        return File(cloudWriter.virtualRootPlus(name)).apply {
            createNewFile()
        }
    }
}