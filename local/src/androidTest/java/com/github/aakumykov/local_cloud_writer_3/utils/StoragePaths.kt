package com.github.aakumykov.local_cloud_writer_3.utils

import android.os.Environment

val storageRootPath: String
    get() = Environment
        .getExternalStorageDirectory().absolutePath


val downloadsPath: String
    get() = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        .absolutePath


