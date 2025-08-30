package com.github.aakumykov.yandex_disk_cloud_writer_3

import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.ConfigReader
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.YANDEX_AUTH_TOKEN_KEY
import org.junit.Assert
import org.junit.Test

class YandexAuthTokenUnitTest {

    @Test
    fun yandex_auth_token_for_tests_is_not_empty() {
        localProperties.apply {
            Assert.assertTrue("local.properties contains yandex auth token key", containsKey(YANDEX_AUTH_TOKEN_KEY))
            val token = get(YANDEX_AUTH_TOKEN_KEY)!!
            Assert.assertTrue("$YANDEX_AUTH_TOKEN_KEY is not empty", token.isNotEmpty())
        }
    }
}

val localProperties: Map<String,String> get() {
    val moduleDir = System.getProperty("user.dir")!!
    return ConfigReader(moduleDir, "..\\local.properties").getAll()
}