package org.example.zitadellogin.core.crypto

import java.security.SecureRandom

actual fun secureRandomBytes(size: Int): ByteArray {
    val out = ByteArray(size)
    SecureRandom().nextBytes(out)
    return out
}
