package com.github.aakumykov.cloud_writer_3

import org.junit.Assert
import org.junit.Test
import java.util.UUID
import kotlin.random.Random

class IsDeepPathContainsIllegalNames {



    @Test
    fun detects_empty_string_in_array() {
        Assert.assertTrue(
            CloudWriter.isDeepPathContainsIllegalNames(listOf(CloudWriter.EMPTY_STRING))
        )
    }

    @Test
    fun detects_zero_char_string_in_array() {
        Assert.assertTrue(
            CloudWriter.isDeepPathContainsIllegalNames(listOf(CloudWriter.ZERO_CHAR_STRING))
        )
    }

    @Test
    fun detects_root_string_in_array() {
        Assert.assertTrue(
            CloudWriter.isDeepPathContainsIllegalNames(listOf(CloudWriter.ROOT_DIR_NAME))
        )
    }

    @Test
    fun detects_zero_char_within_some_string() {
        Assert.assertTrue(
            CloudWriter.isDeepPathContainsIllegalNames(listOf(
                randomId,
                "${randomId}${CloudWriter.ZERO_CHAR_STRING}${randomId}",
                randomId
            ))
        )
    }

    @Test
    fun not_detects_in_normal_string() {
        Assert.assertFalse(
            CloudWriter.isDeepPathContainsIllegalNames(listOf(randomId, randomId))
        )
    }


    private val randomId: String get() = UUID.randomUUID().toString()
}