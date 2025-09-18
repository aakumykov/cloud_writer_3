package com.github.aakumykov.yandex_disk_cloud_writer_3

import android.util.Log
import com.github.aakumykov.cloud_writer_3.BasicCloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.copy_between_streams_with_counting.copyBetweenStreamsWithCounting
import com.github.aakumykov.yandex_disk_cloud_writer_3.ext.toCloudWriterException
import com.google.gson.Gson
import com.yandex.disk.rest.json.Link
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.BufferedSink
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class YandexDiskCloudWriter(
    private val apiScheme: String = API_SCHEME,
    private val apiHost: String = API_HOST,
    private val apiPort: Int = 443,
    private val apiPathBase: String = API_PATH_BASE,
    private val authToken: String,
    private val yandexDiskOkhttpClientBuilder: YandexDiskOkhttpClientBuilder,
    override val virtualRootPath: String = CloudWriter.ROOT_DIR_NAME,
    private val gson: Gson = Gson()
)
    : BasicCloudWriter()
{
    private val yandexDiskClient: OkHttpClient by lazy {
        yandexDiskOkhttpClientBuilder.create(authToken).build()
    }

    override suspend fun createOneLevelDir(dirName: String, ignoreAlreadyExists: Boolean): String = suspendCancellableCoroutine { cc ->

        val url = apiURL(queryPairs = arrayOf(
            PARAM_PATH to dirName
        ))

        val request = apiRequest(url) { put(EMPTY_REQUEST_BODY) }

        val call = yandexDiskClient.newCall(request)

        executeCall(call, cc) { response: Response ->
            when(response.code) {
                201 -> cc.resume(absolutePathFor(dirName))
                404 -> {
                    if (ignoreAlreadyExists) cc.resume(absolutePathFor(dirName))
                    else throwCloudWriterException(response)
                }
                409 -> {
                    if (ignoreAlreadyExists) cc.resume(absolutePathFor(dirName))
                    else throwCloudWriterException(response)
                }
                else -> throwCloudWriterException(response)
            }
        }
    }


    override suspend fun createOneLevelDir(
        parentPath: String,
        childName: String
    ): String {
        return createOneLevelDir(CloudWriter.mergeFilePaths(parentPath, childName),)
    }


    override suspend fun createOneLevelDirIfNotExists(dirName: String): String {
        return if (!fileExists(dirName)) createOneLevelDir(dirName,)
        else virtualRootPlus(dirName)
    }

    override suspend fun createOneLevelDirIfNotExists(
        parentPath: String,
        childDirName: String
    ): String {
        return createOneLevelDirIfNotExists(CloudWriter.mergeFilePaths(parentPath, childDirName))
    }



    // TODO: унифицировать с [LocalCloudWriter2.createDeepDir]
    private suspend fun createDeepDirAbsolute(path: String): String {

        // Так как идёт пошаговое создание
        // каталогов из пути вглубь,
        // то нужно отрезать от него "системную"
        // незаписываемую часть.

        val pathToOperate = path.replace(Regex("^${virtualRootPath}/+"),"")

        return iterateOverDirsInPathFromRoot(pathToOperate) { partialPath ->
            createOneLevelDirIfNotExists(partialPath)
        }.let {
            path
        }
    }


    override suspend fun deleteFileOrEmptyDir(dirPath: String): String = suspendCancellableCoroutine{ cc ->

        val absolutePath = virtualRootPlus(dirPath)

        val url = apiURL(
            PARAM_PATH to absolutePath,
            PARAM_PERMANENTLY to VALUE_TRUE
        )

        val request = apiRequest(url) { delete() }

        val call = yandexDiskClient.newCall(request)

        executeCall(call, cc) { response ->
            when(response.code) {
                204 -> {
                    cc.resume(absolutePath)
                }
                else -> {
                    throwCloudWriterException(response)
                }
            }
        }
    }

    private fun <T> executeCall(
        call: Call,
        cancellableContinuation: CancellableContinuation<T>,
        responseProcessingBlock: (response: Response) -> Unit
    ) {
//       println("${TAG}: executeCall(${call.request().let { "${it.method}: ${it.url}" }})")

        try {
            call.execute().use { response: Response ->
                responseProcessingBlock.invoke(response)
            }
        } catch (e: CancellationException) {
            call.cancel()
        } catch (e: Exception) {
            cancellableContinuation.resumeWithException(e)
        }
    }

    private suspend fun createDeepDirIfNotExistAbsolute(path: String): String {
        return if (fileExists(path)) path
        else createDeepDirAbsolute(path)
    }


    override suspend fun fileExists(path: String): Boolean = suspendCancellableCoroutine { cc ->

        val url = pathApiURL(path)

        val request = apiRequest(url) {}

        val call = yandexDiskClient.newCall(request)

        executeCall(call, cc) { response ->
            when(response.code) {
                200 -> cc.resume(true)
                404 -> cc.resume(false)
                else -> throwCloudWriterException(response)
            }
        }
    }

    override suspend fun fileExists(
        dirPath: String,
        fileName: String
    ): Boolean {
        return fileExists(CloudWriter.mergeFilePaths(dirPath, fileName))
    }


    private fun throwCloudWriterException(response: Response) {
        throw response.toCloudWriterException
    }

    private fun apiURL(vararg queryPairs: Pair<String, String>): HttpUrl {
        return apiURL(url = apiUrlResources, queryPairs = queryPairs)
    }


    private fun apiURL(
        url: String,
        vararg queryPairs: Pair<String, String>
    ): HttpUrl {
        return url.toHttpUrl().newBuilder().apply {
            for ((key,value) in queryPairs) {
                addQueryParameter(key, value)
            }
        }.build()
    }


    private fun pathApiURL(path: String): HttpUrl = apiURL(PARAM_PATH to path)


    private inline fun apiRequest(
        url: HttpUrl,
        requestMethodBlock: Request.Builder.() -> Unit
    ): Request {
        return Request.Builder()
            .url(url)
            .apply {
                requestMethodBlock.invoke(this)
            }.build()
    }


    @Throws(IOException::class, com.github.aakumykov.cloud_writer_3.CloudWriterException::class)
    private suspend fun getURLForUpload(targetFilePath: String, overwriteIfExists: Boolean): String = suspendCancellableCoroutine{ cc ->

        val url = apiURL(apiUrlUpload,
            PARAM_PATH to targetFilePath,
            PARAM_OVERWRITE to overwriteIfExists.toString()
        )

        val request = apiRequest(url) {}

        val call = yandexDiskClient.newCall(request)

        executeCall(call, cc) { response ->
            when(response.code) {
                200 -> cc.resume(linkFromResponse(response))
                else -> throwCloudWriterException(response)
            }
        }
    }


    @Throws(IOException::class, com.github.aakumykov.cloud_writer_3.CloudWriterException::class)
    override suspend fun putStream(
        inputStream: InputStream,
        targetPath: String,
        isRelative: Boolean,
        overwriteIfExists: Boolean,
        readingCallback: ((Long) -> Unit)?,
        writingCallback: ((Long) -> Unit)?,
        finishCallback: ((Long, Long) -> Unit)?
    ) {
        val path = if (isRelative) virtualRootPlus(targetPath) else targetPath

        val uploadURL = getURLForUpload(path, overwriteIfExists)

        return suspendCancellableCoroutine { cc ->

            val requestBody: RequestBody = object: RequestBody() {

                override fun contentType(): MediaType = DEFAULT_MEDIA_TYPE

                override fun writeTo(sink: BufferedSink) {

                    // TODO: вызывать здесь или в copyBetweenStreamsWithCounting
                    //  ошибку и смотреть, что будет.

                    copyBetweenStreamsWithCounting(
                        inputStream = inputStream,
                        outputStream = sink.outputStream(),
                        readingCallback = readingCallback,
                        writingCallback = writingCallback,
                        finishCallback = finishCallback,
                    )
                }
            }

            val url = apiURL(uploadURL)

            val request = apiRequest(url) { put(requestBody) }

            val call = yandexDiskClient.newCall(request)

            cc.invokeOnCancellation { cause ->
                Log.i(TAG, cause?.message ?: "cc.invokeOnCancellation")
                call.cancel()
            }

            executeCall(call, cc) { response: Response ->
                when(response.code) {
                    201 -> {
                        cc.resume(Unit)
                    }
                    else -> throwCloudWriterException(response)
                }
            }
        }
    }


    override suspend fun renameFileOrEmptyDir(
        fromPath: String,
        toPath: String,
        overwriteIfExists: Boolean
    ): Boolean = suspendCancellableCoroutine { cc ->

        val realFromPath = virtualRootPlus(fromPath)
        val realToPath = virtualRootPlus(toPath)

        val url = apiURL(apiUrlMove,
            PARAM_FROM to realFromPath,
            PARAM_PATH to realToPath,
            PARAM_OVERWRITE to overwriteIfExists.toString(),
            PARAM_FORCE_ASYNC to VALUE_FALSE
        )

        val request = apiRequest(url) { post(EMPTY_REQUEST_BODY) }

        val call = yandexDiskClient.newCall(request)

        executeCall(call, cc) { response: Response ->
            when(response.code) {
                201 -> cc.resume(true)
                else -> throwCloudWriterException(response)
            }
        }
    }

    private fun linkFromResponse(response: Response): String {
        return gson.fromJson(response.body?.string(), Link::class.java).href
    }


    val apiPathResources get() = "${apiPathBase}/resources"
    val apiPathMove get() = "${apiPathResources}/move"

    private val apiUrlResources get() = "${apiScheme}://${apiHost}:${apiPort}${apiPathResources}"
    private val apiUrlUpload get() = "${apiUrlResources}/upload"
    private val apiUrlMove get() = "${apiUrlResources}/move"


    companion object {
        val TAG: String = YandexDiskCloudWriter::class.java.simpleName

        const val API_SCHEME = "https"
        const val API_HOST = "cloud-api.yandex.net"
        const val API_PATH_BASE = "/v1/disk"

        const val PARAM_PATH = "path"
        const val PARAM_FROM = "from"
        private const val PARAM_FORCE_ASYNC = "force_async"
        private const val PARAM_OVERWRITE = "overwrite"
        private const val PARAM_PERMANENTLY = "permanently"

        private const val VALUE_TRUE = "true"
        private const val VALUE_FALSE = "false"

        private val DEFAULT_MEDIA_TYPE: MediaType = "application/octet-stream".toMediaType()
        private val EMPTY_REQUEST_BODY by lazy { "".toRequestBody(null) }

        const val AUTH_HEADER_KEY = "Authorization"
    }


    @Throws(IllegalArgumentException::class)
    private fun checkDeepNameForBadParts(deepName: List<String>) {
        if (isDeepPathContainsIllegalNames(deepName))
            throw IllegalArgumentException("Argument contains illegal element: $deepName")
    }
}