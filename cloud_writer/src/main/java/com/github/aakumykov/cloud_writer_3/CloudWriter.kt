package com.github.aakumykov.cloud_writer_3

import com.github.aakumykov.cloud_writer_3.extensions.stripMultiSlashes
import java.io.IOException
import java.io.InputStream

/**
 * Писчик в облако должен:
 * 00) [проверять наличие каталогов] [fileExists]
 * 05) [проверять наличие файлов] [fileExists]
 * 10) создавать каталоги
 *  [createOneLevelDir]
 *  [createOneLevelDirIfNotExists]
 *  [createDeepDir]
 *  [createDeepDirIfNotExists]
 * 15) загружать файлы (потоки?) [putStream]
 * 20) удалять пустые каталоги [deleteFileOrEmptyDir]
 * 30) удалять файлы [deleteFileOrEmptyDir]
 * 40) переименовывать каталоги [renameFileOrEmptyDir]
 * 50) переименовывать файлы [renameFileOrEmptyDir]
 * 60) TODO: перемещать пустые каталоги
 * 70) TODO: перемещать файлы
 */

interface CloudWriter {

    val virtualRootPath: String

    /**
     * Проверяет наличие файла/каталога.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun fileExists(path: String, isAbsolute: Boolean = false): Boolean


    @Throws(IOException::class, CloudWriterException::class)
    suspend fun fileExists(dirPath: String, fileName: String, isAbsolute: Boolean = false): Boolean


    /**
     * Создаёт каталог по указанному пути.
     * @param dirPath Путь к создаваемому каталогу.
     * @param isRelative Признак того, что [dirPath] является относительным.
     * @return абсолютный путь к созданному каталогу.
     * @throws CloudWriterException если каталог не создан, в том числе по
     * причине того, что он уже существует. Для работы без ошибки в случае
     * наличия каталога, используйте метод [createOneLevelDirIfNotExists].
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDir(dirPath: String, isAbsolute: Boolean = false): String

    /**
     * Аналогично методу [createDir](dirPath: String, isAbsolute: Boolean)
     * @param parentPath Путь к родительскому каталогу.
     * @param childName Имя дочернего каталога в родительском.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDir(parentPath: String, childName: String, isAbsolute: Boolean): String



    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDirIfNotExists(dirPath: String, isAbsolute: Boolean = false): String


    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDirIfNotExists(parentPath: String, childDirName: String, isAbsolute: Boolean = false): String


    /**
     * Создаёт многоуровневый каталог путём последовательного создания "вглубь".
     * Существование этого метода обусловлено тем, что облачные API, по крайней мере,
     * YandexDisk.REST_API, не позволяют создавать вложенные каталоги за один раз.
     * Что-ж, из-за этого всем другим, даже методам работы с локальной файловой
     * системой (?), придётся действовать так же.
     *
     * @return Абсолютный путь к созданному каталогу.
     * @throws [IOException], [CloudWriterException]
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createDeepDir(parentPath: String, deepDirName: String): String



    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createDeepDirIfNotExists(dirPath: String, isAbsolute: Boolean): String



    /**
     * Удаляет пустой каталог.
     * В случае, если производится попытка удаления непустого каталога,
     * поведение зависит от реализации:
     * * локальная файловая система - будет выброшено исключение,
     *   каталог не будет удалён.
     * * облако - каталог будет отправлен на удаление в асинхронном режиме
     *   без сигнала о завершении.
     * @return Абсолютный путь к удалённому каталогу.
     */
    suspend fun deleteFileOrEmptyDir(dirPath: String, isRelative: Boolean): String


    /**
     * @param writingCallback Колбек, возвращающий количество записанных байт.
     * @param finishCallback Колбек, возвращающий количество прочитанных и записанных байт.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun putStream(
        inputStream: InputStream,
        targetPath: String,
        isRelative: Boolean,
        overwriteIfExists: Boolean = false,
        readingCallback: ((Long) -> Unit)? = null,
        writingCallback: ((Long) -> Unit)? = null,
        finishCallback: ((Long,Long) -> Unit)? = null,
    )



    /**
     * Переименовывает файл или пустой каталог.
     *
     * (!) Не работает с локальным хранилищем, если целевой
     * файл находится на физическом разделе, отличном
     * от исходного.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun renameFileOrEmptyDir(
        fromPath: String,
        toPath: String,
        isRelative: Boolean,
        overwriteIfExists: Boolean = true
    ): Boolean



    companion object {
        /**
         * Directory separator.
         */
        const val DS = "/"
        const val EMPTY_STRING = ""

        fun mergeFilePaths(vararg paths: String): String
            = paths.joinToString(DS).stripMultiSlashes()
    }
}