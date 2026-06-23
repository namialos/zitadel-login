package org.example.zitadellogin.presentation.keyclocklogin.utils

object Pkce {

    fun createVerifier(): String {
        val bytes = randomBytes(64)

        return bytes.base64UrlEncode()
    }

    fun createChallenge(verifier: String): String {
        val digest = sha256(
            verifier.encodeToByteArray()
        )

        return digest.base64UrlEncode()
    }
}

expect fun randomBytes(size: Int): ByteArray

expect fun sha256(input: ByteArray): ByteArray

expect fun ByteArray.base64UrlEncode(): String