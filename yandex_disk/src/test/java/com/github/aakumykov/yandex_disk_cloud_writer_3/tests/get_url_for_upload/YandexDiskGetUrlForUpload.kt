package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.get_url_for_upload

import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_GET
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.tests.put_stream.YandexDiskPutStream
import com.github.aakumykov.yandex_disk_cloud_writer_3.tests.put_stream.YandexDiskPutStream.Companion
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Test
import java.net.URL

class YandexDiskGetUrlForUpload : YandexDiskBase() {

    //
    // Проверка запросов
    //
    @Test
    fun get_url_for_upload_request() {

        val fileName = randomId

        listOf(true, false).forEach { doOverride ->

            MockResponse()
                .setResponseCode(200)
                .setBody(GET_UPLOAD_URL_JSON_RESPONSE)
                .also { mockWebServer.enqueue(it) }

            runBlocking {
                mockCloudWriter.getURLForUpload(fileName,false)
            }

            checkRequest(
                recordedRequest = mockWebServer.takeRequest(),
                httpMethod = HTTP_METHOD_GET,
                requestUrl = mockCloudWriter.apiPathUpload,
                YandexDiskCloudWriter.PARAM_PATH to fileName,
                YandexDiskCloudWriter.PARAM_OVERWRITE to doOverride.toString(),
            )
        }
    }


    //
    // Проверка возвращаемого значения
    //
    @Test
    fun get_url_for_upload_returned_value() = runBlocking {
        listOf(true, false).forEach { doOverride ->
            // Проверяю, что URL есть URL
            URL(realCloudWriter.getURLForUpload(randomId, doOverride))
        }
    }

    companion object {
        private const val URL_FOR_UPLOAD = "https://uploader64sas.disk.yandex.net:443/upload-target/20250918T170516.865.utd.eue6mn0pltl0qdkdd233j05ja-k64sas.175088"
        private const val GET_UPLOAD_URL_JSON_RESPONSE = "{'method':'PUT','href':$URL_FOR_UPLOAD,'templated':false,'operation_id':'1a4e120c3f11ce2235f1ca519433d4048aa67df133d29e635fb8a0159a3e6c92'}"
    }
}