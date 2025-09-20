package com.github.aakumykov.local_cloud_writer_3

import androidx.core.util.Supplier
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
    override suspend fun createOneLevelDir(dirName: String, ignoreAlreadyExists: Boolean): String {
        return with(File(virtualRootPlus(dirName))) {
            val dirAbsolutePath = this.absolutePath
            if (this.exists()) {
                if (ignoreAlreadyExists) dirAbsolutePath
                else throw CloudWriterException("Dir '$dirAbsolutePath' already exists!")
            } else {
                if (mkdir()) dirAbsolutePath
                else throw CloudWriterException("Dir '$dirAbsolutePath' was not created!")
            }
        }
    }

    override suspend fun createOneLevelDir(parentPath: String, childName: String): String {
        return createOneLevelDir(CloudWriter.mergeFilePaths(parentPath, childName),)
    }


    override suspend fun createOneLevelDirIfNotExists(dirName: String): String {
        return if (!fileExists(dirName)) {
            createOneLevelDir(dirName,)
        }
        else {
            absolutePathFor(dirName)
        }
    }

    override suspend fun createOneLevelDirIfNotExists(parentPath: String, childDirName: String): String {
        return createOneLevelDirIfNotExists(CloudWriter.mergeFilePaths(parentPath,childDirName))
    }

    override suspend fun deleteFileOrEmptyDir(dirPath: String): String {
        val absolutePath = virtualRootPlus(dirPath)
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


    override suspend fun getURLForUpload(
        targetFilePath: String,
        overwriteIfExists: Boolean
    ): String = throw NotImplementedError("Not used in this implementation.")


    @Throws(IOException::class, CloudWriterException::class)
    override suspend fun putStream(
        inputStream: InputStream,
        targetPathProvider: Supplier<String>,
        overwriteIfExists: Boolean,
        readingCallback: ((Long) -> Unit)?,
        writingCallback: ((Long) -> Unit)?,
        finishCallback: ((Long, Long) -> Unit)?,
    ) {
        return suspendCancellableCoroutine { cc ->

            val path = virtualRootPlus(targetPathProvider.get())

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
        overwriteIfExists: Boolean
    ): Boolean = suspendCoroutine { continuation ->

        val realFromPath = virtualRootPlus(fromPath)
        val realToPath = virtualRootPlus(toPath)

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