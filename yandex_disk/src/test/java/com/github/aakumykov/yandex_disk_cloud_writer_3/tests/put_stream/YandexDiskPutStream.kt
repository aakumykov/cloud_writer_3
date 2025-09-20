package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.put_stream

import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class YandexDiskPutStream : YandexDiskBase() {

    companion object {
//        private const val UPLOAD_URL_HOST = "uploader64sas.disk.yandex.net"
        private const val UPLOAD_URL_HOST = "127.0.0.1"
        private const val GET_UPLOAD_URL_JSON_RESPONSE = "{'method':'PUT','href':'https://${UPLOAD_URL_HOST}:443/upload-target/20250918T170516.865.utd.eue6mn0pltl0qdkdd233j05ja-k64sas.175088','templated':false,'operation_id':'1a4e120c3f11ce2235f1ca519433d4048aa67df133d29e635fb8a0159a3e6c92'}"
    }

    private val randomBytes get() = Random.nextBytes(1024)

    //
    // Проверка запросов
    //

    // По сути, здесь проверяется запрос внутреннего метода
    // на получение URL для загрузки файла.
    @Test
    fun put_stream_requests() {

        fun uploadUrlJSON(host: String, port: Int): String {
            return "{'method':'PUT','href':'https://${host}:${port}/upload-target/20250918T170516.865.utd.eue6mn0pltl0qdkdd233j05ja-k64sas.175088','templated':false,'operation_id':'1a4e120c3f11ce2235f1ca519433d4048aa67df133d29e635fb8a0159a3e6c92'}"
        }

        listOf(true, false).forEach { doOverride ->

            // Ответ запрос URL для загрузки файла.
            MockResponse()
                .setResponseCode(200)
                .setBody(uploadUrlJSON(mockWebServer.hostName,mockWebServer.port))
                .also { mockWebServer.enqueue(it) }

            // Ответ на отправку файла (потока).
            enqueueResponseCodes(210)

            val fileName = randomId

            randomBytes.inputStream().use { inputStream ->
                runBlocking {
                    mockCloudWriter.putStream(
                        inputStream = inputStream,
                        targetPathProvider = { runBlocking {
                            mockCloudWriter.getURLForUpload(fileName, doOverride)
                        }},
                        overwriteIfExists = doOverride
                    )
                }
            }

//        val r1 = mockWebServer.takeRequest()
//        val r2 = mockWebServer.takeRequest()

            /*checkRequest(
                mockWebServer.takeRequest(),
                httpMethod = HTTP_METHOD_GET,
                mockCloudWriter.apiPathUpload,
                YandexDiskCloudWriter.PARAM_PATH to mockCloudWriter.absolutePathFor(fileName)
            )*/

            /*checkRequest(
                httpMethod = HTTP_METHOD_POST,
                mockCloudWriter.apiPathUpload,
            )*/

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
}