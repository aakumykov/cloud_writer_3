package com.github.aakumykov.cloud_writer_3

import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test

class MergeFilePaths {

    /**
     * План тестирования:
     * - одна пустая строка соединяются в такую же [one_empty_string_is_merged_to_itself]
     * - две и более пустых строк соединяются в корневой каталог
     *   (точнее, разделитель между ними) [many_empty_strings_are_merged_to_root_path]
     * - корневой путь соединяется в корневой путь [one_root_path_is_merged_to_root_path]
     * - много корневых путей соединяются в корневой путь [many_root_paths_are_merged_to_root_path]
     *  - одна непустая строка соединяется в себя же [one_not_empty_string_is_merged_to_itself]
     *  - много непустых строк соединяются в корректный путь [merge_file_names_to_correct_path]
     */

    companion object {
        private const val ROOT_PATH = "/"
        private const val EMPTY_STRING = ""
        private const val FILE_NAME = "file"
    }

    @Test
    fun one_empty_string_is_merged_to_itself() {
        Assert.assertEquals(
            EMPTY_STRING,
            CloudWriter.mergeFilePaths(EMPTY_STRING),
        )
    }

    @Test
    fun many_empty_strings_are_merged_to_root_path() {
        for (i in 2..10) {
            val strings = Array(i) { EMPTY_STRING  }
            Assert.assertEquals(
                ROOT_PATH,
                CloudWriter.mergeFilePaths(*strings)
            )
        }
    }


    @Test
    fun one_root_path_is_merged_to_root_path() {
        Assert.assertEquals(
            ROOT_PATH,
            CloudWriter.mergeFilePaths(ROOT_PATH, ROOT_PATH)
        )
    }


    @Test
    fun many_root_paths_are_merged_to_root_path() {
        for (i in 2..10) {
            val strings = Array(i) { EMPTY_STRING  }
            Assert.assertEquals(
                ROOT_PATH,
                CloudWriter.mergeFilePaths(*strings)
            )
        }
    }


    @Test
    fun one_not_empty_string_is_merged_to_itself() {
        Assert.assertEquals(
            FILE_NAME,
            CloudWriter.mergeFilePaths(FILE_NAME)
        )
    }


    @Test
    fun merge_file_names_to_correct_path() {
        for (i in 2..10) {
            val strings = Array(i) { FILE_NAME  }
            val expectedPath = strings.joinToString(CloudWriter.DS)
            Assert.assertEquals(
                "Merge '${strings}' to '$expectedPath'",
                expectedPath,
                CloudWriter.mergeFilePaths(*strings)
            )
        }
    }
}