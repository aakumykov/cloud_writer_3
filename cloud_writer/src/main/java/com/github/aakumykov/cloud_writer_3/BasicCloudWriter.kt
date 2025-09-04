package com.github.aakumykov.cloud_writer_3

abstract class BasicCloudWriter : CloudWriter {

    abstract override val virtualRootPath: String

    protected fun virtualRootPlus(dirName: String): String {
        return CloudWriter.mergeFilePaths(virtualRootPath, dirName)
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
            .split(CloudWriter.DS)
            .let { it }
            .filterNot { "" == it }
            .let { it }
            .reduce { acc, s ->
                action(acc)
                acc + CloudWriter.DS + s
            }.let { tailDir: String ->
                action(tailDir)
                path
            }
    }
}