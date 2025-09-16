package com.github.aakumykov.yandex_disk_cloud_writer_3.utils

import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

@Throws(NoSuchElementException::class)
fun MockWebServer.takeNthRequest(n: Int): RecordedRequest {
    var req: RecordedRequest? = null
    repeat(n) { req = takeRequest() }
    return req!!
}