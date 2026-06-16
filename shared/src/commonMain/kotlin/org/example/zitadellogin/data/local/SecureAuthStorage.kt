package org.example.zitadellogin.data.local

import org.example.zitadellogin.domain.model.TokenBundle

expect class SecureAuthStorage() {
    fun saveTokens(tokens: TokenBundle?)
    fun readTokens(): TokenBundle?
    fun savePendingPkce(codeVerifier: String, state: String)
    fun readPendingPkce(): Pair<String, String>?
    fun clearPendingPkce()
    fun clear()
}
