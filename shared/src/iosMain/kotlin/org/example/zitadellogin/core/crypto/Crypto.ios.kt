package org.example.zitadellogin.core.crypto

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.arc4random_buf

@OptIn(ExperimentalForeignApi::class)
actual fun secureRandomBytes(size: Int): ByteArray {
    val out = ByteArray(size)
    out.usePinned { pinned ->
        arc4random_buf(pinned.addressOf(0), size.toULong())
    }
    return out
}
