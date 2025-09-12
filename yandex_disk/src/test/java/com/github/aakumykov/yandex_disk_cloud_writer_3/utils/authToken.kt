package com.github.aakumykov.yandex_disk_cloud_writer_3.utils

import com.github.aakumykov.yandex_disk_cloud_writer_3.localProperties

const val YANDEX_AUTH_TOKEN_KEY = "yandex_disk_auth_token_for_tests"

val authToken: String
    get() = localProperties.get(YANDEX_AUTH_TOKEN_KEY)!!