package com.github.aakumykov.local_cloud_writer_3

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.local_cloud_writer_3.base.StorageAccessTestCase
import com.github.aakumykov.local_cloud_writer_3.utils.localCloudWriter
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class LocalCloudWriterFileExistsInstrumentedTest : StorageAccessTestCase() {

    private val cloudWriter: CloudWriter by lazy {
        localCloudWriter
    }

    @Test
    fun relative_root_dir_exists(): Unit = runBlocking {
        Assert.assertTrue(
            cloudWriter.fileExists(ROOT_DIR, true)
        )
    }

    @Test
    fun unique_relative_path_does_not_exists(): Unit = runBlocking {
        Assert.assertFalse(
            cloudWriter.fileExists(
                uniquePath,
                true
            )
        )
    }

    companion object {
        const val ROOT_DIR = "/"
        val uniquePath: String get() = UUID.randomUUID().toString()
    }
}