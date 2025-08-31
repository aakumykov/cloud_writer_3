package com.github.aakumykov.local_cloud_writer_3.utils

import com.github.aakumykov.local_cloud_writer_3.LocalCloudWriter

val localCloudWriter: LocalCloudWriter
    get() = LocalCloudWriter(virtualRootPath = storageRootPath)

