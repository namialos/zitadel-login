package org.example.zitadellogin.presentation.keyclocklogin.utils

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault

@OptIn(ExperimentalForeignApi::class)
actual fun randomBytes(size: Int): ByteArray {
    val bytes = ByteArray(size)

    bytes.usePinned {
        SecRandomCopyBytes(
            kSecRandomDefault,
            size.toULong(),
            it.addressOf(0)
        )
    }

    return bytes
}

@OptIn(ExperimentalForeignApi::class)
actual fun sha256(input: ByteArray): ByteArray {
    val output = ByteArray(32)

    /*input.usePinned { inputPinned ->
        output.usePinned { outputPinned ->
            CC_SHA256(
                inputPinned.addressOf(0),
                input.size.toUInt(),
                outputPinned.addressOf(0)
            )
        }
    }
*/
    return output
}

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.base64UrlEncode(): String {
    val data = NSData.create(
        bytes = this.refTo(0) as COpaquePointer?,
        length = size.toULong()
    )

    return data.base64EncodedStringWithOptions(0u)
        .replace("+", "-")
        .replace("/", "_")
        .replace("=", "")
}
