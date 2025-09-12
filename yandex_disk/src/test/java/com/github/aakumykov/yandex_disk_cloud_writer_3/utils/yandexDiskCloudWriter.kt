package com.github.aakumykov.yandex_disk_cloud_writer_3.utils

import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskOkHttpClientCreator

val yandexDiskCloudWriter: YandexDiskCloudWriter
    get() = YandexDiskCloudWriter(
        authToken = authToken,
        yandexDiskClientCreator = YandexDiskOkHttpClientCreator(okHttpClientBuilder)
    )