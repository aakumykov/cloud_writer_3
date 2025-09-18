package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.delete

import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_DELETE
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class YandexDiskDeleteFileOrEmptyDir : YandexDiskBase() {

    //
    // Проверка возвращаемого значения
    //
    @Test
    fun delete_empty_dir(): Unit = runBlocking {
        val dirName = randomId
        realCloudWriter.createOneLevelDir(dirName)
        checkReturnedValue(dirName, realCloudWriter.deleteFileOrEmptyDir(dirName))
    }


    //
    // Проверка выброса исключений
    //
    @Test
    fun throws_exception_on_deleting_unexistsent_dir() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                realCloudWriter.deleteFileOrEmptyDir(randomId)
            }
        }
    }


    //
    // Проверка запросов
    //
    @Test
    fun deleting_existing_dir_requests() {
        val dirName = randomId
        enqueueResponseCodes(204)
        runBlocking {
            mockCloudWriter.deleteFileOrEmptyDir(dirName)
        }
        checkRequest(
            recordedRequest = mockWebServer.takeRequest(),
            httpMethod = HTTP_METHOD_DELETE,
            mockCloudWriter.apiPathResources
        )
    }
}