package com.github.aakumykov.cloud_writer_3.utils_for_tests

class RingList<T>(private val elements: List<T>) {

    val size: Int
        get() = elements.size

    fun get(index: Int): T {
        if (elements.isEmpty()) throw NoSuchElementException("RingList is empty.")
        return elements[actualIndexFor(index)]
    }

    fun actualIndexFor(index: Int): Int = (index % size + size) % size
}