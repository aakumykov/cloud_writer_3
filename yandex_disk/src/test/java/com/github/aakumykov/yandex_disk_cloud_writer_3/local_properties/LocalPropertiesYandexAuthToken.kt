package com.github.aakumykov.yandex_disk_cloud_writer_3.local_properties

import com.github.aakumykov.yandex_disk_cloud_writer_3.YANDEX_SDK_AUTH_TOKEN_KEY
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.LocalPropertyReader
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class LocalPropertiesYandexAuthToken {

    @Test
    fun local_properties_contains_non_empty_yandex_auth_token() {
        Assert.assertTrue(
            LocalPropertyReader.getLocalProperty(YANDEX_SDK_AUTH_TOKEN_KEY).isNotEmpty()
        )
    }

    @Test
    fun local_properties_not_contains_unexistent_key() {
        val randomKey = UUID.randomUUID().toString()
        Assert.assertThrows(NoSuchElementException::class.java) {
            LocalPropertyReader.getLocalProperty(randomKey)
        }
    }
}