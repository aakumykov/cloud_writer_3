package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import org.junit.Test

class YandexDiskCreateOneLevelDir : YandexDiskBase() {

    @Test
    fun create_one_level_dir_with_simple_name_request(): Unit = runBlocking {
        val dirName = randomId
        mockWebServer.enqueue(MockResponse().setResponseCode(201))
        mockCloudWriter.createOneLevelDir(dirName)
        checkRequest(mockWebServer.takeRequest(), dirName)
    }


    @Test
    fun create_one_level_dir_with_simple_name_result(): Unit = runBlocking {
        val dirName = randomId
        checkResult(dirName, realCloudWriter.createOneLevelDir(dirName))
    }


    @Test
    fun create_one_level_dir_with_with_parent_child_name_request(): Unit = runBlocking {
        val parentName = randomId
        val childName = randomId
        val fullRelativePath = CloudWriter.mergeFilePaths(parentName, childName)
        mockWebServer.enqueue(MockResponse().setResponseCode(201))
        mockCloudWriter.createOneLevelDir(parentName, childName)
        checkRequest(mockWebServer.takeRequest(), fullRelativePath)
    }


    @Test
    fun create_one_level_dir_with_parent_child_name_result(): Unit = runBlocking {
        val parentName = randomId
        val childName = randomId
        val fullRelativePath = CloudWriter.mergeFilePaths(parentName, childName)
        realCloudWriter.createOneLevelDir(parentName)
        checkResult(fullRelativePath, realCloudWriter.createOneLevelDir(parentName, childName))
    }


    private fun checkResult(requestedDirPath: String, resultingDirPath: String) {
        Assert.assertEquals(
            realCloudWriter.absolutePathFor(requestedDirPath),
            resultingDirPath
        )
    }

    private fun checkRequest(recordedRequest: RecordedRequest, dirName: String, ) {
        Assert.assertEquals(
            HTTP_METHOD_PUT,
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