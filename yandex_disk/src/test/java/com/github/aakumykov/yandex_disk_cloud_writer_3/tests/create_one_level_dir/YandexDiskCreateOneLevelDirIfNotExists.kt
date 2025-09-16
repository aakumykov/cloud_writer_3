package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.HTTP_METHOD_GET
import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Test

class YandexDiskCreateOneLevelDirIfNotExists : YandexCreateDirBase() {

    // TODO: обрабатывать ошибочные и вырожденные случаи.

    //
    // Запрос на создание каталога, что нового, что существующего, идентичны.
    //
    @Test
    fun create_dir_with_simple_name_request(): Unit = runBlocking {
        val dirName = randomId
        // Коды на проверку существования и создание каталога.
        enqueueResponseCodes(200, 201)
        mockCloudWriter.createOneLevelDirIfNotExists(dirName)
        // Метод "createOneLevelDirIfNotExists" внутри себя использует GET-метод
        // "fileExists()", и затем, если нужно, PUT-метод "createOneLevelDir()";
        // здесь записанным запросом оказывается первый из них...
        checkRequest(HTTP_METHOD_GET, YandexDiskCloudWriter.PARAM_PATH to dirName)
    }


    @Test
    fun create_dir_with_parent_child_name_request(): Unit = runBlocking {
        val parentName = randomId
        val childName = randomId
        val fullDirName = CloudWriter.mergeFilePaths(parentName, childName)
        enqueueResponseCodes(200, 201)
        mockCloudWriter.createOneLevelDirIfNotExists(parentName, childName)
        checkRequest(HTTP_METHOD_GET, YandexDiskCloudWriter.PARAM_PATH to fullDirName)
    }


    //
    // Проверка результата создания новых (ещё не существующих) каталогов.
    //
    @Test
    fun create_new_dir_with_simple_name_result(): Unit = runBlocking {
        val dirName = randomId
        checkResult(dirName, realCloudWriter.createOneLevelDirIfNotExists(dirName))
    }


    @Test
    fun create_new_dir_with_parent_child_name_result(): Unit = runBlocking {
        val parentName = randomId
        val childName = randomId
        realCloudWriter.createOneLevelDir(parentName,)
        checkResult(
            CloudWriter.mergeFilePaths(parentName, childName),
            realCloudWriter.createOneLevelDirIfNotExists(parentName, childName)
        )
    }


    //
    // Проверка результата "создания" уже существующих каталогов.
    //
    @Test
    fun create_existing_one_level_dir_with_simple_name_result(): Unit = runBlocking {
        val dirName = randomId
        realCloudWriter.createOneLevelDir(dirName,)
        checkResult(dirName, realCloudWriter.createOneLevelDirIfNotExists(dirName))
    }


    @Test
    fun create_existing_dir_with_parent_child_name_result(): Unit = runBlocking {
        val parentName = randomId
        val childName = randomId
        realCloudWriter.createOneLevelDir(parentName,)
        checkResult(
            CloudWriter.mergeFilePaths(parentName, childName),
            realCloudWriter.createOneLevelDirIfNotExists(parentName, childName)
        )
    }
}