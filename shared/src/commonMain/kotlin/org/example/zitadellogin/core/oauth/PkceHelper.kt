package org.example.zitadellogin.core.oauth

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.example.zitadellogin.core.crypto.secureRandomBytes
import org.kotlincrypto.hash.sha2.SHA256

@OptIn(ExperimentalEncodingApi::class)
object PkceHelper {
    private const val VERIFIER_RANDOM_BYTES = 32

    /** RFC 7636: BASE64URL without padding. */
    private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

    fun generateVerifier(): String {
        val bytes = secureRandomBytes(VERIFIER_RANDOM_BYTES)
        return base64Url.encode(bytes)
    }

    fun challengeForVerifier(verifier: String): String {
        val digest = SHA256().digest(verifier.encodeToByteArray())
        return base64Url.encode(digest)
    }
}

@OptIn(ExperimentalEncodingApi::class)
fun generateOAuthState(): String {
    val bytes = secureRandomBytes(32)
    return Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(bytes)
}
