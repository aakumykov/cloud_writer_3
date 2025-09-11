package com.github.aakumykov.local_cloud_writer_3.base

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.local_cloud_writer_3.utils.localCloudWriter
import com.github.aakumykov.local_cloud_writer_3.utils.randomId
import com.github.aakumykov.local_cloud_writer_3.utils.storageRootPath

abstract class LocalBase : StorageAccessTestCase() {

    protected val cloudWriter: CloudWriter by lazy {
        localCloudWriter(storageRootPath)
    }

    protected fun nameForDeepDir(depth: Int): List<String> = buildList {
        repeat(depth) {
            add(randomId)
        }
    }

    companion object {
        const val ROOT_DIR = "/"
        const val EMPTY_DIR_NAME = ""
        val ILLEGAL_DIR_NAME = String(charArrayOf(Char(0)))
    }
}
