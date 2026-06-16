package org.example.zitadellogin.data.local

import com.liftric.kvault.KVault
import org.example.zitadellogin.core.config.AppConfig
import org.example.zitadellogin.domain.model.TokenBundle

actual class SecureAuthStorage actual constructor() {
    private val vault = KVault(AppConfig.KEYCHAIN_SERVICE, "")

    actual fun saveTokens(tokens: TokenBundle?) {
        if (tokens == null) {
            clear()
            return
        }
        vault.set(key = KEY_ACCESS, stringValue = tokens.accessToken)
        tokens.refreshToken?.let { vault.set(key = KEY_REFRESH, stringValue = it) }
            ?: vault.deleteObject(forKey = KEY_REFRESH)
        tokens.idToken?.let { vault.set(key = KEY_ID, stringValue = it) }
            ?: vault.deleteObject(forKey = KEY_ID)
        tokens.expiresInSeconds?.let { vault.set(key = KEY_EXPIRES, longValue = it) }
            ?: vault.deleteObject(forKey = KEY_EXPIRES)
    }

    actual fun readTokens(): TokenBundle? {
        val access = vault.string(forKey = KEY_ACCESS) ?: return null
        return TokenBundle(
            accessToken = access,
            refreshToken = vault.string(forKey = KEY_REFRESH),
            idToken = vault.string(forKey = KEY_ID),
            expiresInSeconds = vault.long(forKey = KEY_EXPIRES),
        )
    }

    actual fun savePendingPkce(codeVerifier: String, state: String) {
        vault.set(key = KEY_PKCE_VERIFIER, stringValue = codeVerifier)
        vault.set(key = KEY_PKCE_STATE, stringValue = state)
    }

    actual fun readPendingPkce(): Pair<String, String>? {
        val verifier = vault.string(forKey = KEY_PKCE_VERIFIER) ?: return null
        val state = vault.string(forKey = KEY_PKCE_STATE) ?: return null
        return verifier to state
    }

    actual fun clearPendingPkce() {
        vault.deleteObject(forKey = KEY_PKCE_VERIFIER)
        vault.deleteObject(forKey = KEY_PKCE_STATE)
    }

    actual fun clear() {
        vault.clear()
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_ID = "id_token"
        const val KEY_EXPIRES = "expires_in"
        const val KEY_PKCE_VERIFIER = "pkce_verifier"
        const val KEY_PKCE_STATE = "pkce_state"
    }
}
