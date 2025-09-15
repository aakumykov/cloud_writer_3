package com.github.aakumykov.local_cloud_writer_3

import com.github.aakumykov.cloud_writer_3.BasicCloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriter
import com.github.aakumykov.cloud_writer_3.CloudWriterException
import com.github.aakumykov.copy_between_streams_with_counting.copyBetweenStreamsWithCounting
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocalCloudWriter(
    override val virtualRootPath: String,
)
    : BasicCloudWriter()
{
    override suspend fun createOneLevelDir(dirName: String): String {
        return with(File(virtualRootPlus(dirName))) {
            val dirAbsolutePath = this.absolutePath
            if (!mkdir())
                throw CloudWriterException("Dir '$dirAbsolutePath' was not created")
            dirAbsolutePath
        }
    }

    override suspend fun createOneLevelDir(parentPath: String, childName: String): String {
        return createOneLevelDir(CloudWriter.mergeFilePaths(parentPath, childName))
    }


    override suspend fun createOneLevelDirIfNotExists(dirName: String): String {
        return if (!fileExists(dirName)) {
            createOneLevelDir(dirName)
        }
        else {
            absolutePathFor(dirName)
        }
    }

    override suspend fun createOneLevelDirIfNotExists(parentPath: String, childDirName: String): String {
        return createOneLevelDirIfNotExists(CloudWriter.mergeFilePaths(parentPath,childDirName))
    }

    override suspend fun deleteFileOrEmptyDir(dirPath: String, isRelative: Boolean): String {
        return if (isRelative) deleteEmptyDirAbsolute(virtualRootPlus(dirPath))
        else deleteEmptyDirAbsolute(dirPath)
    }

    private fun deleteEmptyDirAbsolute(absolutePath: String): String {
        return File(absolutePath).delete().let { isDeleted ->
            if (isDeleted) absolutePath
            else throw CloudWriterException("Dir '$absolutePath' was not deleted.")
        }
    }

    override suspend fun fileExists(path: String): Boolean {
        val realPath = virtualRootPlus(path)
        val file = File(realPath)
        return file.exists()
    }


    override suspend fun fileExists(dirPath: String, fileName: String): Boolean {
        return fileExists(CloudWriter.mergeFilePaths(dirPath, fileName))
    }


    @Throws(IOException::class, CloudWriterException::class)
    override suspend fun putStream(
        inputStream: InputStream,
        targetPath: String,
        isRelative: Boolean,
        overwriteIfExists: Boolean,
        readingCallback: ((Long) -> Unit)?,
        writingCallback: ((Long) -> Unit)?,
        finishCallback: ((Long,Long) -> Unit)?,
    ) {
        return suspendCancellableCoroutine { cc ->

            val path = if (isRelative) virtualRootPlus(targetPath) else targetPath

            val targetFile = File(path)

            if (targetFile.exists() && !overwriteIfExists)
                cc.resume(Unit)

            copyBetweenStreamsWithCounting(
                inputStream = inputStream,
                outputStream = targetFile.outputStream(),
                readingCallback = readingCallback,
                writingCallback = writingCallback,
                finishCallback = finishCallback,
            ).also {
                cc.resume(Unit)
            }
        }
    }


    override suspend fun renameFileOrEmptyDir(
        fromPath: String,
        toPath: String,
        isRelative: Boolean,
        overwriteIfExists: Boolean
    ): Boolean = suspendCoroutine { continuation ->

        val realFromPath = if (isRelative) virtualRootPlus(fromPath) else fromPath
        val realToPath = if (isRelative) virtualRootPlus(toPath) else toPath

        val targetFile = File(realToPath)

        continuation.resume(
            if (!overwriteIfExists && targetFile.exists()) false
            else File(realFromPath).renameTo(targetFile)
        )
    }


    /*@Throws(IllegalArgumentException::class)
    private fun checkDeepNameForBadParts(deepName: List<String>) {
        if (isDeepPathContainsIllegalNames(deepName))
            throw IllegalArgumentException("Argument contains illegal element: $deepName")
    }*/
}