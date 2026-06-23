package org.example.zitadellogin.presentation.keyclocklogin.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

actual fun randomBytes(size: Int): ByteArray {
    return ByteArray(size).apply {
        SecureRandom().nextBytes(this)
    }
}

actual fun sha256(input: ByteArray): ByteArray {
    return MessageDigest
        .getInstance("SHA-256")
        .digest(input)
}

actual fun ByteArray.base64UrlEncode(): String {
    return Base64.encodeToString(
        this,
        Base64.URL_SAFE or
                Base64.NO_WRAP or
                Base64.NO_PADDING
    )
}