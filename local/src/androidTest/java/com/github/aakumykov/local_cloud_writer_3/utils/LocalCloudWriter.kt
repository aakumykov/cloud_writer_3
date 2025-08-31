package com.github.aakumykov.local_cloud_writer_3.utils

import com.github.aakumykov.local_cloud_writer_3.LocalCloudWriter

fun localCloudWriter(virtualRootPath: String): LocalCloudWriter
    = LocalCloudWriter(virtualRootPath = virtualRootPath)

