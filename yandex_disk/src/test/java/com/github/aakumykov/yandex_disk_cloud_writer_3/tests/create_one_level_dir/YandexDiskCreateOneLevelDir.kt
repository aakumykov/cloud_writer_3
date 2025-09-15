package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.ROOT_PATH
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import org.junit.Test

class YandexDiskCreateOneLevelDir : YandexCreateDirBase() {

    // TODO: обрабатывать ошибочные и вырожденные случаи.

    //
    // Проверка запросов
    //
    @Test
    fun create_one_level_dir_with_simple_name_request(): Unit = runBlocking {
        val dirName = randomId
        enqueueResponseCodes(201)
        mockCloudWriter.createOneLevelDir(dirName)
        checkRequest(HTTP_METHOD_PUT, dirName)
    }

    @Test
    fun create_one_level_dir_with_with_parent_child_name_request(): Unit = runBlocking {
        val parentName = randomId
        val childName = randomId
        val fullRelativePath = CloudWriter.mergeFilePaths(parentName, childName)
        enqueueResponseCodes(201)
        mockCloudWriter.createOneLevelDir(parentName, childName)
        checkRequest(HTTP_METHOD_PUT, fullRelativePath)
    }


    //
    // Проверка возвращаемого результата
    //
    @Test
    fun create_one_level_dir_with_simple_name_result(): Unit = runBlocking {
        val dirName = randomId
        checkResult(dirName, realCloudWriter.createOneLevelDir(dirName))
    }

    @Test
    fun create_one_level_dir_with_parent_child_name_result(): Unit = runBlocking {
        val parentName = randomId
        val childName = randomId
        val fullRelativePath = CloudWriter.mergeFilePaths(parentName, childName)
        realCloudWriter.createOneLevelDir(parentName)
        checkResult(fullRelativePath, realCloudWriter.createOneLevelDir(parentName, childName))
    }


    //
    // TODO: Проверка ошибок конфликта
    //
    @Test
    fun create_existing_dir_throws_exception_1() {
        val dirName = randomId
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                realCloudWriter.createOneLevelDir(dirName)
                realCloudWriter.createOneLevelDir(dirName)
            }
        }
    }

    @Test
    fun create_existing_dir_throws_exception_2() {
        val dirName = randomId
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                realCloudWriter.createOneLevelDir(ROOT_PATH, dirName)
                realCloudWriter.createOneLevelDir(ROOT_PATH, dirName)
            }
        }
    }
}