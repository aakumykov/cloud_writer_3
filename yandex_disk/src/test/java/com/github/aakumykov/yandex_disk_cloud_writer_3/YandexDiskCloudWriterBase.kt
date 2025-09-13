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


    protected val mockCloudWriter: YandexDiskCloudWriter by lazy {

        val mockUrl = mockWebServer.url(YandexDiskCloudWriter.API_PATH_BASE)

        YandexDiskCloudWriter(
            apiScheme = mockUrl.scheme,
            apiHost = mockUrl.host,
            apiPort = mockUrl.port,
            apiPathBase = mockUrl.encodedPath,
            authToken = yandexAuthToken,
            yandexDiskOkhttpClientBuilder = YandexDiskOkhttpClientBuilder(okHttpClientBuilder)
        )
    }

    protected val realCloudWriter: YandexDiskCloudWriter by lazy {
        YandexDiskCloudWriter(
            authToken = yandexAuthToken,
            yandexDiskOkhttpClientBuilder = YandexDiskOkhttpClientBuilder(okHttpClientBuilder)
        )
    }


    @Before
    fun prepareCloudWriter() {
        mockWebServer.apply {
            start()
            enqueue(EMPTY_MOCK_RESPONSE)
        }
    }


    @After
    fun stopServer() {
        mockWebServer.close()
    }

    companion object {
        private val EMPTY_MOCK_RESPONSE = MockResponse()
    }
}