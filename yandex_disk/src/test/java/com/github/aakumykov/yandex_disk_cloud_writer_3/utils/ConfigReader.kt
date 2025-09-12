package com.github.aakumykov.yandex_disk_cloud_writer_3.utils

import java.io.File
import java.util.Properties


class ConfigReader(
    private val dirName: String?,
    private val fileName: String
) {
    @Throws(NoSuchElementException::class)
    fun getPropertyValue(key: String): String {
        return File(dirName, fileName).inputStream().use { input ->
            Properties().let {
                it.load(input)
                if (key in it.keys) it.getProperty(key)
                else throw NoSuchElementException("Property '$key' not found.")
            }
        }
    }
}