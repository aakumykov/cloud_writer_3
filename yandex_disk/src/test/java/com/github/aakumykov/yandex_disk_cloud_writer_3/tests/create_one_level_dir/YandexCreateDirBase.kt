package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import org.junit.Assert

abstract class YandexCreateDirBase : YandexDiskBase() {

    protected fun checkResult(requestedDirPath: String, resultingDirPath: String) {
        Assert.assertEquals(
            realCloudWriter.absolutePathFor(requestedDirPath),
            resultingDirPath
        )
    }

    protected fun checkRequest(
        httpMethod: String,
        dirName: String
    ) {
        val recordedRequest = mockWebServer.takeRequest()

        Assert.assertEquals(
            httpMethod,
            recordedRequest.method
        )

        Assert.assertEquals(
            realCloudWriter.apiPathResources,
            recordedRequest.requestUrl?.encodedPath
        )

        Assert.assertTrue(
            recordedRequest.requestUrl
                ?.queryParameterNames
                ?.contains(YandexDiskCloudWriter.PARAM_PATH)
                ?: false
        )

        Assert.assertEquals(
            dirName,
            recordedRequest.requestUrl?.queryParameter(YandexDiskCloudWriter.PARAM_PATH)
        )
    }
}
