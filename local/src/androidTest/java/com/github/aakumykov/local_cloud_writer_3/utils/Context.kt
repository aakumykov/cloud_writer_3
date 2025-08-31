package com.github.aakumykov.local_cloud_writer_3.utils

import android.content.Context
import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry

val targetContext: Context
    get() = InstrumentationRegistry
        .getInstrumentation()
        .targetContext

