package com.github.aakumykov.cloud_writer_3

import com.github.aakumykov.cloud_writer_3.utils_for_tests.RingList
import org.junit.Assert
import org.junit.Test

class RingListTest {

    /**
     * План тестирования:
     *
     * А) Тесты метода [RingList.actualIndexFor]:
     * - проверка с положительными индексами [actual_index_for_works_correct_with_positive_values]
     * - проверка с отрицательными индексами [actual_index_for_works_correct_with_negative_values]
     *
     * Б) Тесты получения элементов:
     * - проверка размера [ring_size_equals_orig_list_size]
     *
     * - доступ по положительным индексам возвращает верные элементы [positive_access_returns_valid_elements]
     * - доступ по отрицательным индексам возвращает верные элементы [negative_access_returns_valid_elements]
     *
     * - случайный доступ по положительным индексам [random_access_with_positive_indices]
     * - случайный доступ по отрицательным индексам [random_access_with_negative_indices]
     *
     * - доступ по положительным индексам с выходом за максимальный размер [access_with_long_range_positive_indices]
     * - доступ по отрицательным индексам с выходом за максимальный размер [access_with_long_range_negative_indices]
     */

    private val indicesCheckingMultiplier = 100
    private val randomAccessCheckingRepeats = 10

    private val elements = List(5) { i -> "string-$i" }
    private val ringList: RingList<String> = RingList(elements)


    @Test
    fun actual_index_for_works_correct_with_positive_values() {
        val existingIndices = List(ringList.size) { it }

        val longRangeIndicesForChecking = List(ringList.size * indicesCheckingMultiplier) { it }
        val expectedIndices = List(indicesCheckingMultiplier) { existingIndices }.flatten()

        val expectedString = expectedIndices.joinToString("") { ringList.get(it) }
        val actualString = longRangeIndicesForChecking.joinToString("") { ringList.get(it) }

        Assert.assertEquals(expectedString, actualString)
    }

    @Test
    fun actual_index_for_works_correct_with_negative_values() {
        val existingIndices = List(ringList.size) { it }

        val checkedIndices = List(ringList.size * indicesCheckingMultiplier) { -1 * (it+1) }

        val expectedActualIndices = List(indicesCheckingMultiplier) {
            existingIndices.reversed().map { it }
        }.flatten()

        val testedActualIndices = checkedIndices.map { ringList.actualIndexFor(it) }

        Assert.assertEquals(
            expectedActualIndices.joinToString(""),
            testedActualIndices.joinToString("")
        )
    }



    @Test
    fun ring_size_equals_orig_list_size() {
        Assert.assertEquals(elements.size ,ringList.size)
    }



    @Test
    fun positive_access_returns_valid_elements() {
        val expected = elements.joinToString("")
        val actual = StringBuilder().apply {
            repeat(ringList.size) { i -> append(ringList.get(i)) }
        }.toString()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun negative_access_returns_valid_elements() {
        val expected = elements.reversed().joinToString("")
        val actual = StringBuilder().apply {
            repeat(ringList.size) { i -> append(ringList.get(ringList.size-(i+1))) }
        }.toString()
        Assert.assertEquals(expected, actual)
    }



    @Test
    fun random_access_with_positive_indices() {
        repeat(ringList.size * randomAccessCheckingRepeats) {
            val indices = List(ringList.size) { it }.shuffled()
            val expected = indices.joinToString("") { elements[it] }
            val actual = indices.joinToString("") { ringList.get(it) }
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun random_access_with_negative_indices() {
        repeat(ringList.size * randomAccessCheckingRepeats) {
            val indices = List(ringList.size) { -1 * (it+1) }.shuffled()
            val expected = indices.joinToString("") { elements[ringList.actualIndexFor(it)] }
            val actual = indices.joinToString("") { ringList.get(it) }
            Assert.assertEquals(expected, actual)
        }
    }


    /**
     * По сути, методы [access_with_long_range_positive_indices] и
     * [access_with_long_range_negative_indices] повторяют проверки, применённые для
     * проверки метода [RingList.actualIndexFor], только немного по-другому.
     */
    @Test
    fun access_with_long_range_positive_indices() {
        val testedIndices = List(indicesCheckingMultiplier * ringList.size) { it }
        val expectedString = testedIndices.map{ ringList.get(ringList.actualIndexFor(it)) }.joinToString("")
        val actualString = testedIndices.map { ringList.get(it) }.joinToString("")
        Assert.assertEquals(expectedString, actualString)
    }

    @Test
    fun access_with_long_range_negative_indices() {
        val testedIndices = List(indicesCheckingMultiplier * ringList.size) { -1 * (it+1) }
        val expectedString = testedIndices.map { ringList.get(ringList.actualIndexFor(it)) }.joinToString("")
        val actualString = testedIndices.map { ringList.get(it) }.joinToString("")
        Assert.assertEquals(expectedString, actualString)
    }
}