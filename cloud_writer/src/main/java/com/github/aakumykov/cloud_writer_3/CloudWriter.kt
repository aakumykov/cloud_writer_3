package com.github.aakumykov.cloud_writer_3

import androidx.core.util.Supplier
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

    val validFileNameRegex: Regex

    // TODO: тестировать!
    fun absolutePathFor(dirName: String): String {
        return mergeFilePaths(virtualRootPath, dirName)
    }

    fun absolutePathFor(deepDirNames: List<String>): String {
        return mergeFilePaths(virtualRootPath, * deepDirNames.toTypedArray())
    }

    /**
     * Проверяет, что в наборе имён, составляющих путь к "глубокому каталогу", нет запрещённых элементов.
     * К которым относятся:
     * 1) строка нулевой длины ("") и корневой каталог ("/") --> ведёт к изменению запрашиваемого пути;
     * 2) нулевой символ --> запрещён в файловой системе unix-подобных систем.
     */
    fun isDeepPathContainsIllegalNames(deepDirNames: List<String>): Boolean {
        return deepDirNames
            .map { dirName ->
                if (dirName in arrayOf(ROOT_DIR_NAME, EMPTY_STRING)) true
                else if (dirName.contains(ZERO_CHAR_STRING, true)) true
                else false
            }
            .contains(true)
    }


    /**
     *
     */
    suspend fun iterateOverDeepDirParts(deepDirName: List<String>,
                                        intermediatePathProcessor: suspend (String) -> Unit,
                                        finalPathProcessor: suspend (String) -> String
    ): String {
        return deepDirName.reduce { currentPathIntoDeep, nextDirIntoDeep ->
            // Действия над промежуточными каталогами.
            intermediatePathProcessor.invoke(currentPathIntoDeep)
            mergeFilePaths(currentPathIntoDeep, nextDirIntoDeep)
        }.let { fullDeepPath ->
            // Действие над конечным каталогом.
            finalPathProcessor.invoke(fullDeepPath)
        }.let { it }
    }




    /**
     * Проверяет наличие файла/каталога.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun fileExists(path: String): Boolean


    @Throws(IOException::class, CloudWriterException::class)
    suspend fun fileExists(dirPath: String, fileName: String): Boolean


    /**
     * Создаёт одноуровневый каталог в виртуальном корне ([virtualRootPath]),
     * установленном при создании экземпляра CloudWriter-а.
     *
     * ВАЖНО: метод не предназначен для создания цепочки вложенных каталогов, так
     * как облачные реализации (по крайней мере, Яндекс.Диск) могут не поддерживать это.
     * Так, попытка создания сразу "каталог-1/каталог-2/каталог-3" окажется провальной.
     * Вместо этого используйте метод [createDeepDir].
     *
     * @param dirName Имя создаваемого каталога. Может быть вложенным ("dir1/dir2",
     * но при условии, что "dir1" уже существует.
     * @param ignoreAlreadyExists Игнорировать ошибку "уже существует".
     * Используется в процессе пошагового создания "глубокого" каталога.
     * @return абсолютный путь к созданному каталогу.
     * @throws CloudWriterException если каталог не создан, в том числе по
     * причине того, что он уже существует. Для работы без ошибки в случае
     * наличия каталога, используйте метод [createOneLevelDirIfNotExists].
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDir(dirName: String, ignoreAlreadyExists: Boolean = false): String


    /**
     * Аналогичен методу [createOneLevelDir]
     * @param parentPath Путь к родительскому каталогу.
     * @param childName Имя дочернего каталога в родительском.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDir(parentPath: String, childName: String): String



    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDirIfNotExists(dirName: String): String


    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createOneLevelDirIfNotExists(parentPath: String, childDirName: String): String


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
    suspend fun createDeepDir(deepDirName: List<String>): String {

        if (isDeepPathContainsIllegalNames(deepDirName))
            throw IllegalArgumentException("Argument contains illegal element: $deepDirName")

        if (deepDirName.isEmpty())
            return virtualRootPath

        return iterateOverDeepDirParts(
            deepDirName,
            { createOneLevelDir(it, ignoreAlreadyExists = true) },
            { createOneLevelDir(it) }
        )
    }


    @Throws(IOException::class, CloudWriterException::class)
    suspend fun createDeepDirIfNotExists(deepName: List<String>): String {

        // Несмотря на то, что эта функция, по сути, обёртка над [createDeepDir] нельзя оставлять
        // проверку аргумента только там. Здесь этот аргумент используется в методе [fileExists],
        // корректная работа которого также критически важна.
        if (isDeepPathContainsIllegalNames(deepName))
            throw IllegalArgumentException("Argument contains illegal element: $deepName")

        val mergedRelativeName = mergeFilePaths(* deepName.toTypedArray())

        return if (!fileExists(mergedRelativeName)) {
            createDeepDir(deepName)
        }
        else {
            virtualRootPlus(mergedRelativeName)
        }
    }


    fun virtualRootPlus(dirName: String): String {
        return mergeFilePaths(virtualRootPath, dirName)
    }


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
    suspend fun deleteFileOrEmptyDir(dirPath: String): String


    /**
     * Запрашивает URL для последующей загрузки файл на сервер.
     * Актуально не для всех реализаций (например, не используется в локальной (Local)).
     */
    suspend fun getURLForUpload(targetFilePath: String, overwriteIfExists: Boolean): String


    /**
     * @param writingCallback Колбек, возвращающий количество записанных байт.
     * @param finishCallback Колбек, возвращающий количество прочитанных и записанных байт.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun putStream(
        inputStream: InputStream,
        targetPathProvider: Supplier<String>,
        overwriteIfExists: Boolean,
        readingCallback: ((Long) -> Unit)? = null,
        writingCallback: ((Long) -> Unit)? = null,
        finishCallback: ((Long, Long) -> Unit)? = null,
    )


    /**
     * Переименовывает файл или пустой каталог.
     *
     * // FIXME: в методе нет защиты от переименовывания непустого каталога.
     *
     * (!) Не работает с локальным хранилищем, если целевой
     * файл находится на физическом разделе, отличном
     * от исходного.
     */
    @Throws(IOException::class, CloudWriterException::class)
    suspend fun renameFileOrEmptyDir(
        fromPath: String,
        toPath: String,
        overwriteIfExists: Boolean = true
    ): Boolean



    companion object {
        /**
         * Directory separator.
         */
        const val DS = "/"
        const val EMPTY_STRING = ""
        const val ROOT_DIR_NAME = DS
        val ZERO_CHAR_STRING = String(charArrayOf(Char(0)))

        fun mergeFilePaths(vararg paths: String): String = paths.joinToString(DS).stripMultiSlashes()

        fun mergeFilePaths(paths: List<String>): String = mergeFilePaths(* paths.toTypedArray())
    }
}