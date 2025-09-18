package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.put_stream

import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_GET
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_POST
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class YandexDiskPutStream : YandexDiskBase() {

    companion object {
        private const val GET_UPLOAD_URL_JSON_RESPONSE = "{'method':'PUT','href':'https://uploader64sas.disk.yandex.net:443/upload-target/20250918T170516.865.utd.eue6mn0pltl0qdkdd233j05ja-k64sas.175088','templated':false,'operation_id':'1a4e120c3f11ce2235f1ca519433d4048aa67df133d29e635fb8a0159a3e6c92'}"
    }

    private val randomBytes get() = Random.nextBytes(1024)

    //
    // Проверка запросов
    //

    // По сути, здесь проверяется запрос внутреннего метода
    // на получение URL для загрузки файла.
    @Test
    fun put_stream_requests() = runBlocking {
        val fileName = randomId

        // Ответ запрос URL для загрузки файла.
        MockResponse()
            .setResponseCode(200)
            .setBody(GET_UPLOAD_URL_JSON_RESPONSE)
            .also { mockWebServer.enqueue(it) }

        // Ответ на отправку файла (потока).
//        enqueueResponseCodes(210)

        // FIXME: !!! откуда берётся код 201 во второй части работы метода???

        randomBytes.inputStream().use { inputStream ->
            mockCloudWriter.putStream(
                inputStream = inputStream,
                targetPath = fileName
            )
        }

        checkRequest(
            httpMethod = HTTP_METHOD_GET,
            mockCloudWriter.apiPathUpload,
            YandexDiskCloudWriter.PARAM_PATH to mockCloudWriter.absolutePathFor(fileName)
        )

        /*checkRequest(
            httpMethod = HTTP_METHOD_POST,
            mockCloudWriter.apiPathUpload,
        )*/
    }


    //
    // Проверка результата работы (файла)
    //
    @Test
    fun putting_stream_produces_file() {
        val fileName = randomId
        runBlocking {
            randomBytes.inputStream().use { inputStream ->
                realCloudWriter.putStream(
                    inputStream = inputStream,
                    targetPath = fileName,
                    overwriteIfExists = true
                )
                Assert.assertTrue(
                    realCloudWriter.fileExists(fileName)
                )
            }
        }
    }
}