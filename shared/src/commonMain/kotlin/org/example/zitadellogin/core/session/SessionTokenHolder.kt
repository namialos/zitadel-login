package org.example.zitadellogin.core.session

/**
 * Thread-safe access for bearer token attached to outbound API calls.
 */
interface SessionTokenHolder {
    fun setToken(value: String?)
    fun snapshot(): String?
}
