package com.github.aakumykov.cloud_writer_3

import com.github.aakumykov.cloud_writer_3.extensions.stripMultiSlashes
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class StripMultiSlashes {

    /**
     * План тестирования:
     * - пустая строка [empty_string_remains_empty];
     * - строка только из слешей [slashes_only_string_becomes_one_slash_string];
     * - много слешей в начале строки [multi_slashes_at_start_of_string];
     * - много слешей в конце строки [multi_slashes_at_end_of_string];
     * - множественные слеши в нескольких местах строки [multi_slashes_at_multiple_places];
     */

    companion object {
        private const val EMPTY_STRING = ""
        private const val SOME_NAME = "some_name"
        private const val SLASH = CloudWriter.DS
    }

    @Test
    fun empty_string_remains_empty() {
        Assert.assertEquals(
            EMPTY_STRING,
            EMPTY_STRING.stripMultiSlashes()
        )
    }


    @Test
    fun slashes_only_string_becomes_one_slash_string() {
        for (i in 1..10) {
            Assert.assertEquals(
                SLASH,
                SLASH.repeat(i).stripMultiSlashes()
            )
        }
    }

    @Test
    fun multi_slashes_at_start_of_string() {
        for (i in 1..10) {
            val expectedString = "${SLASH}${SOME_NAME}"
            Assert.assertEquals(
                expectedString,
                "${SLASH.repeat(i)}${SOME_NAME}".stripMultiSlashes()
            )
        }
    }


    @Test
    fun multi_slashes_at_end_of_string() {
        for (i in 1..10) {
            val expectedString = "${SOME_NAME}${SLASH}"
            Assert.assertEquals(
                expectedString,
                "${SOME_NAME}${SLASH.repeat(i)}".stripMultiSlashes()
            )
        }
    }


    @Test
    fun multi_slashes_at_multiple_places() {

        // Двадцать проверок.
        for (i in 1..20) {

            val names = Array(i) { n -> "${SOME_NAME}-${n+1}" }

            val separators: MutableList<String> = MutableList(i) { randomMultipleSlashes }

            val expectedString = names.joinToString(SLASH)

            val testedString = StringBuilder().apply {
                repeat(names.size - 1) { i ->
                    append(names[i])
                    append(separators[i])
                }
                append(names.last())
            }.toString()

            Assert.assertEquals(expectedString, testedString.stripMultiSlashes())
        }
    }

    private val randomIntFrom1To5: Int get() = Random.nextInt(1, 6)
    private val randomMultipleSlashes: String get() = SLASH.repeat(randomIntFrom1To5)
}