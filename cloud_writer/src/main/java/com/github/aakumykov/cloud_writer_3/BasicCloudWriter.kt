package com.github.aakumykov.cloud_writer_3

abstract class BasicCloudWriter : CloudWriter {

    companion object {
        private const val ROOT_DIR_NAME = CloudWriter.DS
        private val ZERO_CHAR_STRING = String(charArrayOf(Char(0)))
    }

    abstract override val virtualRootPath: String

    override val validFileNameRegex: Regex = Regex("^[^$ZERO_CHAR_STRING]$")

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
    @Deprecated("к удалению")
    protected suspend fun iterateOverDirsInPathFromRoot(path: String, action: suspend (String) -> Unit): String {
        return path
            .split(CloudWriter.DS)
            .filterNot { "" == it }
            .reduce { acc, s ->
                action(acc)
                acc + CloudWriter.DS + s
            }.let { tailDir: String ->
                action(tailDir)
                path
            }
    }


    override fun isIllegal(dirName: String): Boolean {
        return if (dirName in arrayOf(ROOT_DIR_NAME, ZERO_CHAR_STRING)) true
        else if (dirName.contains(ZERO_CHAR_STRING, true)) true
        else false
    }

    override fun isIllegal(deepDirNames: List<String>): Boolean = deepDirNames.any { isIllegal(it) }
}