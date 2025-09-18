package com.github.aakumykov.yandex_disk_cloud_writer_3

import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.LocalPropertyReader
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before

abstract class YandexDiskBase {

    protected val mockWebServer: MockWebServer by lazy { MockWebServer() }


    private val yandexAuthToken: String
        get() = LocalPropertyReader.getLocalProperty(YANDEX_SDK_AUTH_TOKEN_KEY)


    private val okHttpClientBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder()



    @Before
    fun prepareCloudWriter() {
        mockWebServer.start()
    }


    @After
    fun stopServer() {
        mockWebServer.close()
    }


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


    protected fun checkRequest(
        recordedRequest: RecordedRequest,
        httpMethod: String,
        requestUrl: String,
        vararg queryParameters: Pair<String,String>
    ) {

        Assert.assertEquals(
            httpMethod,
            recordedRequest.method
        )

        Assert.assertEquals(
            requestUrl,
            recordedRequest.requestUrl?.encodedPath
        )

        queryParameters.forEach { pair ->
            Assert.assertEquals(
                pair.second,
                recordedRequest.requestUrl?.queryParameter(pair.first)
            )
        }
    }


    protected fun checkRequest(
        httpMethod: String,
        requestUrl: String,
        vararg queryParameters: Pair<String,String>
    ) {
        checkRequest(
            recordedRequest = mockWebServer.takeRequest(),
            httpMethod = httpMethod,
            requestUrl = requestUrl,
            * queryParameters
        )
    }


    protected fun checkReturnedValue(requestedDirPath: String, resultingDirPath: String) {
        Assert.assertEquals(
            realCloudWriter.absolutePathFor(requestedDirPath),
            resultingDirPath
        )
    }


    protected fun enqueueResponseCodes(vararg code: Int) {
        println("${TAG}: enqueueResponseCodes(${code.joinToString(",")})")
        code.forEach {
            mockWebServer.enqueue(MockResponse().setResponseCode(it))
        }
    }

    companion object {
        val TAG = YandexDiskBase::class.simpleName
    }
}