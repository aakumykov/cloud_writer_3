package com.github.aakumykov.cloud_writer_3

import com.github.aakumykov.cloud_writer_3.extensions.stripMultiSlashes

abstract class BasicCloudWriter : com.github.aakumykov.cloud_writer_3.CloudWriter {

    abstract override val virtualRootPath: String

    override fun virtualRootPlus(vararg pathParts: String): String {
        return mutableListOf(virtualRootPath)
            .apply {
                if (!addAll(pathParts.toList()))
                    throw RuntimeException("Cannot add path parts to virtual root path.")
            }
            .joinToString(com.github.aakumykov.cloud_writer_3.CloudWriter.DS)
            .stripMultiSlashes()
    }

    /**
     * Проходит путь [path] от корня в грубину, вызывая действие
     * [action] на каждой итерации.
     * @return Первоначальное значение [path].
     *
     * Пример:
     *
     * Если [path] = /dir1/dir2/dir3, то блок [action] будет вызван три раза с параметрами:
     * 1) "dir1"
     * 2) "dir1/dir2"
     * 3) "dir1/dir2/dir3"
     */
    protected suspend fun iterateOverDirsInPathFromRoot(path: String, action: suspend (String) -> Unit): String {
        return path
            .split(com.github.aakumykov.cloud_writer_3.CloudWriter.DS)
            .filterNot { "" == it }
            .reduce { acc, s ->
                action(acc)
                acc + com.github.aakumykov.cloud_writer_3.CloudWriter.DS + s
            }.let { tailDir: String ->
                action(tailDir)
                path
            }
    }
}