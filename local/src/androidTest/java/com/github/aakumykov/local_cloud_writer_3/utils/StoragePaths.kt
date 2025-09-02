package com.github.aakumykov.local_cloud_writer_3.utils

import android.os.Environment

val storageRootPath: String
    get() = Environment
        .getExternalStorageDirectory().absolutePath


val downloadsDirPath: String
    get() = standardDirAbsolutePath(Environment.DIRECTORY_DOWNLOADS)


val musicDirPath: String
    get() = standardDirAbsolutePath(Environment.DIRECTORY_MUSIC)


fun standardDirAbsolutePath(dirPath: String): String {
    return Environment
        .getExternalStoragePublicDirectory(dirPath)
        .absolutePath
}

