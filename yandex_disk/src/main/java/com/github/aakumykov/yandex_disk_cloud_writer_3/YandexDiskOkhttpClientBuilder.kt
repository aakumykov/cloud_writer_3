package com.github.aakumykov.yandex_disk_cloud_writer_3

import okhttp3.OkHttpClient

class YandexDiskOkhttpClientBuilder(
    private val clientBuilder: OkHttpClient.Builder
) {
    fun create(authToken: String): OkHttpClient.Builder {
        return clientBuilder.addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().apply {
                header(YandexDiskCloudWriter.AUTH_HEADER_KEY, authToken)
            }.build())
        }
    }
}