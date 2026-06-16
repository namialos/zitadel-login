package org.example.zitadellogin.core.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter

object AppLogger {
    private const val CHUNK_SIZE = 3_000

    private val config = StaticConfig(
        minSeverity = Severity.Debug,
        logWriterList = listOf(platformLogWriter()),
    )
    private val logger = Logger(config, "ZitadelLogin")

    fun d(message: String, throwable: Throwable? = null) {
        if (throwable != null) logger.d(throwable) { message } else logger.d { message }
    }

    fun i(message: String, throwable: Throwable? = null) {
        if (throwable != null) logger.i(throwable) { message } else logger.i { message }
    }

    fun w(message: String, throwable: Throwable? = null) {
        if (throwable != null) logger.w(throwable) { message } else logger.w { message }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) logger.e(throwable) { message } else logger.e { message }
    }

    /** Prints full payload to logcat (split into chunks for Android 4K limit). */
    fun dump(tag: String, data: String) {
        if (data.isEmpty()) {
            i("[$tag] (empty)")
            return
        }
        if (data.length <= CHUNK_SIZE) {
            i("[$tag] $data")
            return
        }
        val chunks = data.chunked(CHUNK_SIZE)
        chunks.forEachIndexed { index, chunk ->
            i("[$tag] part ${index + 1}/${chunks.size}: $chunk")
        }
    }
}
