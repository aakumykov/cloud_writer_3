package com.github.aakumykov.yandex_disk_cloud_writer_3

import okhttp3.Interceptor
import okhttp3.OkHttpClient

class YandexDiskOkHttpClientCreator(
    private val clientBuilder: OkHttpClient.Builder
) {
    fun create(authToken: String): OkHttpClient = clientBuilder
        .addNetworkInterceptor { chain: Interceptor.Chain ->
            val builder = chain.request().newBuilder()
                .header("Authorization", authToken)
            chain.proceed(builder.build())
        }.build()
}