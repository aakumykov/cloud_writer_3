package com.github.aakumykov.yandex_disk_cloud_writer_3

import java.io.File
import java.util.Properties


class ConfigReader(
    private val dirName: String,
    private val fileName: String
) {
    private val contents: MutableMap<String,String> = mutableMapOf()

    private val configFile: File get() = File(dirName, fileName)

    private val properties: Properties get() = configFile.inputStream().use { input ->
        Properties().apply { load(input) }
    }

    fun getPropertyValue(key: String): String = properties.getProperty(key)

    fun getAll(): Map<String,String> {
        properties.apply {
            this.keys.map { it.toString() }.forEach { key ->
                contents[key] = this[key].toString()
            }
        }
        return contents
    }
}