package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.rename

import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_POST
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_PUT
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RenameFileOrEmptyDir : YandexDiskBase() {

    //
    // Проверка запросов
    // TODO: проверять поведение в ответ на коды ошибок
    //
    @Test
    fun rename_dir_request() {
        val oldName = randomId
        val newName = randomId

        enqueueResponseCodes(201)

        runBlocking {
            mockCloudWriter.renameFileOrEmptyDir(oldName, newName)
        }

        checkRequest(
            recordedRequest = mockWebServer.takeRequest(),
            httpMethod = HTTP_METHOD_POST,
            mockCloudWriter.apiPathMove,
            YandexDiskCloudWriter.PARAM_FROM to mockCloudWriter.absolutePathFor(oldName),
            YandexDiskCloudWriter.PARAM_PATH to mockCloudWriter.absolutePathFor(newName)
        )
    }

    //
    // Проверка возвращаемого значения
    //
    @Test
    fun rename_file_or_empty_dir_returned_value() = runBlocking {
        val oldName = randomId
        val newName = randomId
        realCloudWriter.createOneLevelDir(oldName)
        Assert.assertTrue(realCloudWriter.renameFileOrEmptyDir(oldName, newName))
    }

    //
    // Проверка выброса исключения
    // TODO: при отсутствии авторизации
    //
    @Test
    fun throws_exception_on_no_source_dir() {
        Assert.assertThrows(CloudWriterException::class.java) {
            runBlocking {
                realCloudWriter.renameFileOrEmptyDir(randomId, randomId)
            }
        }
    }
}