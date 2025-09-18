package com.github.aakumykov.yandex_disk_cloud_writer_3.tests.create_dir

import com.github.aakumykov.yandex_disk_cloud_writer_3.YandexDiskBase
import com.github.aakumykov.yandex_disk_cloud_writer_3.utils.randomId
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert

abstract class YandexCreateDirBase : YandexDiskBase() {

    protected fun nameForDeepDir(depth: Int): List<String> = buildList { repeat(depth) { add(randomId) } }
}
