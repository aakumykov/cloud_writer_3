package com.github.aakumykov.yandex_disk_cloud_writer_3

import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.LocalPropertyReader
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before

abstract class YandexDiskCloudWriterBase {

    protected val mockWebServer: MockWebServer by lazy { MockWebServer() }

    private val yandexAuthToken: String
        get() = LocalPropertyReader.getLocalProperty(YANDEX_SDK_AUTH_TOKEN_KEY)

    private val okHttpClientBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder()


    protected lateinit var yandexDiskCloudWriter: YandexDiskCloudWriter


    @Before
    fun prepareCloudWriter() {

        mockWebServer.start()

        mockWebServer.enqueue(MockResponse())

        yandexDiskCloudWriter = YandexDiskCloudWriter(
            serverUrl = mockWebServer.url(YandexDiskCloudWriter.YANDEX_API_PATH).toString(),
            authToken = yandexAuthToken,
            yandexDiskOkhttpClientBuilder = YandexDiskOkhttpClientBuilder(okHttpClientBuilder)
        )
    }


    @After
    fun stopServer() {
        mockWebServer.close()
    }
}