package com.github.aakumykov.local_cloud_writer_3.base

import java.io.File

abstract class LocalFileCreationBase : LocalBase() {

    protected fun newFile(name: String): File {
        return File(cloudWriter.virtualRootPlus(name)).apply {
            createNewFile()
        }
    }
}