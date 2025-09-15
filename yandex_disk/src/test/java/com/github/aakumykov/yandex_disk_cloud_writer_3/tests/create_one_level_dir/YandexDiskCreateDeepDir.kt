package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_one_level_dir

import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import kotlinx.coroutines.runBlocking
import org.junit.Test

class YandexDiskCreateDeepDir : YandexCreateDirBase() {

    private val maxDeepDirLevel = 10

    //
    // Проверка запросов
    //
    @Test
    fun create_one_level_deep_dir(): Unit = runBlocking {
        repeat(maxDeepDirLevel) { i ->
            enqueueResponseCodes(201)
            val deepDirName = nameForDeepDir(i+1)
            mockCloudWriter.createDeepDir(deepDirName)
//            checkRequest()
        }
    }

}