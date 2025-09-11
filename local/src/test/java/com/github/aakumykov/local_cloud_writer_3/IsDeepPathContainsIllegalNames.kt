package com.github.aakumykov.local_cloud_writer_3

import com.github.aakumykov.cloud_writer_3.CloudWriter
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class IsDeepPathContainsIllegalNames {

    private val cloudWriter: CloudWriter by lazy {
        LocalCloudWriter(CloudWriter.ROOT_DIR_NAME)
    }

    @Test
    fun detects_empty_string_in_array() {
        Assert.assertTrue(
            cloudWriter.isDeepPathContainsIllegalNames(listOf(CloudWriter.EMPTY_STRING))
        )
    }

    @Test
    fun detects_zero_char_string_in_array() {
        Assert.assertTrue(
            cloudWriter.isDeepPathContainsIllegalNames(listOf(CloudWriter.ZERO_CHAR_STRING))
        )
    }

    @Test
    fun detects_root_string_in_array() {
        Assert.assertTrue(
            cloudWriter.isDeepPathContainsIllegalNames(listOf(CloudWriter.ROOT_DIR_NAME))
        )
    }

    @Test
    fun detects_zero_char_within_some_string() {
        Assert.assertTrue(
            cloudWriter.isDeepPathContainsIllegalNames(listOf(
                randomId,
                "${randomId}${CloudWriter.ZERO_CHAR_STRING}${randomId}",
                randomId
            ))
        )
    }

    @Test
    fun not_detects_in_normal_string() {
        Assert.assertFalse(
            cloudWriter.isDeepPathContainsIllegalNames(listOf(randomId, randomId))
        )
    }


    private val randomId: String get() = UUID.randomUUID().toString()
}