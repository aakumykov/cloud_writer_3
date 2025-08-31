package com.github.aakumykov.local_cloud_writer_3.utils

import java.io.File

val tempFile: File get() = File.createTempFile(randomId, randomId)