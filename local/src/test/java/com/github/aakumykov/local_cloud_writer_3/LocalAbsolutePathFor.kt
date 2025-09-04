package com.github.aakumykov.local_cloud_writer_3

import com.github.aakumykov.cloud_writer_3.CloudWriter
import org.junit.Assert
import org.junit.Test

class LocalAbsolutePathFor {

    /**
     * План тестирования:
     * - проверить, что метод соединяет [virtualRootPath] с аргументом.
     * - лишние слеши удаляются
     */


    @Test
    fun absolute_path_for_is_virtual_root_with_child_path() {
        val fakeRootCloudWriter = LocalCloudWriter(FAKE_ROOT_PATH)
        Assert.assertEquals(
            CloudWriter.mergeFilePaths(FAKE_ROOT_PATH, DIR_NAME),
            fakeRootCloudWriter.absolutePathFor(DIR_NAME)
        )
    }

    @Test
    fun extra_slashes_are_removed() {
        val simpleRootCloudWriter = LocalCloudWriter(ROOT_DIR)
        Assert.assertEquals(
            ROOT_DIR,
            simpleRootCloudWriter.absolutePathFor(ROOT_DIR.repeat(5))
        )
    }

    companion object {
        private const val ROOT_DIR = "/"
        private const val FAKE_ROOT_PATH = "fake_root"
        private const val DIR_NAME = "dir1"
    }
}