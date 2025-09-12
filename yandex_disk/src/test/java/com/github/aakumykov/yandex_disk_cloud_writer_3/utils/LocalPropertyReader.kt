package com.github.aakumykov.yandex_disk_cloud_writer_3.utils

class LocalPropertyReader {

    companion object {

        @Throws(NoSuchElementException::class)
        fun getLocalProperty(key: String): String {
            return ConfigReader(
                System.getProperty("user.dir"),
                "..\\local.properties"
            ).getPropertyValue(key)
        }

    }
}