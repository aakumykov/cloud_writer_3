package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert

abstract class YandexCreateDirBase : YandexDiskBase() {

    protected fun checkResult(requestedDirPath: String, resultingDirPath: String) {
        Assert.assertEquals(
            realCloudWriter.absolutePathFor(requestedDirPath),
            resultingDirPath
        )
    }


    protected fun checkRequest(recordedRequest: RecordedRequest, httpMethod: String, vararg queryParameters: Pair<String,String>) {

        Assert.assertEquals(
            httpMethod,
            recordedRequest.method
        )

        Assert.assertEquals(
            realCloudWriter.apiPathResources,
            recordedRequest.requestUrl?.encodedPath
        )

        queryParameters.forEach { pair ->
            Assert.assertEquals(
                pair.second,
                recordedRequest.requestUrl?.queryParameter(pair.first)
            )
        }
    }


    protected fun checkRequest(httpMethod: String, vararg queryParameters: Pair<String,String>) {
        checkRequest(
            mockWebServer.takeRequest(),
            httpMethod,
            * queryParameters
        )
    }

    protected fun nameForDeepDir(depth: Int): List<String> = buildList { add(randomId) }
}
