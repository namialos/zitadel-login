package org.example.zitadellogin.core.crypto

/**
 * Cryptographically suitable random bytes for PKCE verifiers and OAuth state.
 */
expect fun secureRandomBytes(size: Int): ByteArray
