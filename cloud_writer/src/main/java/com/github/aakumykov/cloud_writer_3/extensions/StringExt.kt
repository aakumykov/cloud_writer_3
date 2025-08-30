package com.github.aakumykov.cloud_writer_3.extensions

fun String.stripMultiSlashes(): String = this.replace(Regex("[/]+"),"/")