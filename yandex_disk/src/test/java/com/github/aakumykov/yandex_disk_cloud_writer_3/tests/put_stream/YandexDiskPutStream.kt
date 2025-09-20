package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.put_stream

import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_GET
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.ByteString.Companion.toByteString
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class YandexDiskPutStream : YandexDiskBase() {

    //
    // Проверка запросов
    //
    @Test
    fun put_stream_requests() {

        listOf(true, false).forEach { doOverride ->

            // Получение URL для загрузки файла

            val fileName = randomId

            MockResponse()
                .setResponseCode(200)
                .setBody(mockedJsonWithUploadingURL(mockWebServer))
                .also { mockWebServer.enqueue(it) }

            val uploadingURL = runBlocking {
                mockCloudWriter.getURLForUpload(fileName, doOverride)
            }

            checkRequest(
                recordedRequest = mockWebServer.takeRequest(),
                httpMethod = HTTP_METHOD_GET,
                requestUrlPath = mockCloudWriter.apiPathUpload,
                YandexDiskCloudWriter.PARAM_PATH to fileName,
                YandexDiskCloudWriter.PARAM_OVERWRITE to doOverride.toString()
            )


            // Отправка данных по полученному URL

            val dataToSend = randomBytes

            MockResponse()
                .setResponseCode(201)
                .also { mockWebServer.enqueue(it) }

            dataToSend.inputStream().use { inputStream ->
                runBlocking {
                    mockCloudWriter.putStream(
                        inputStream = inputStream,
                        targetPathProvider = { uploadingURL },
                        overwriteIfExists = doOverride
                    )
                }
            }

            mockWebServer.takeRequest().also { request ->
                // Проверка метаданных запроса
                checkRequest(
                    request,
                    httpMethod = HTTP_METHOD_PUT,
                    requestUrlPath = MOCK_UPLOADING_URL_PATH
                )
                // Проверка данных запроса
                Assert.assertEquals(
                    dataToSend.toByteString(),
                    request.body.readByteString()
                )
            }
        }
    }


    //
    // Проверка наличия результата работы (файла)
    //
    @Test
    fun putting_stream_produces_file() = listOf(true, false).forEach { doOverride ->
        val fileName = randomId
        runBlocking {
            randomBytes.inputStream().use { inputStream ->
                realCloudWriter.putStream(
                    inputStream = inputStream,
                    targetPathProvider = { runBlocking {
                        realCloudWriter.getURLForUpload(fileName, doOverride)
                    } },
                    overwriteIfExists = doOverride
                )
                Assert.assertTrue(
                    realCloudWriter.fileExists(fileName)
                )
            }
        }
    }


    companion object {
        private const val MOCK_OPERATION_ID = "1a4e120c3f11ce2235f1ca519433d4048aa67df133d29e635fb8a0159a3e6c92"
        private const val MOCK_UPLOADING_URL_PATH = "/upload-target/20250918T170516.865.utd.eue6mn0pltl0qdkdd233j05ja-k64sas.175088"
    }

    private fun mockedJsonWithUploadingURL(mockWebServer: MockWebServer): String {
        val href = mockWebServer.url(MOCK_UPLOADING_URL_PATH)
        return "{'method':'PUT','href':'$href','templated':false,'operation_id':'$MOCK_OPERATION_ID'}"
    }

    private val randomBytes get() = Random.nextBytes(1024)

}