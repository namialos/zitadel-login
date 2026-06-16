package org.example.zitadellogin.data.local

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.example.zitadellogin.AndroidContextHolder
import org.example.zitadellogin.core.config.AppConfig
import org.example.zitadellogin.domain.model.TokenBundle

actual class SecureAuthStorage actual constructor() {
    private val context get() = AndroidContextHolder.requireApplicationContext()

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            AppConfig.SECURE_PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    actual fun saveTokens(tokens: TokenBundle?) {
        if (tokens == null) {
            clear()
            return
        }
        prefs.edit()
            .putString(KEY_ACCESS, tokens.accessToken)
            .putString(KEY_REFRESH, tokens.refreshToken)
            .putString(KEY_ID, tokens.idToken)
            .putLong(KEY_EXPIRES, tokens.expiresInSeconds ?: -1L)
            .apply()
    }

    actual fun readTokens(): TokenBundle? {
        val access = prefs.getString(KEY_ACCESS, null) ?: return null
        val expires = prefs.getLong(KEY_EXPIRES, -1L)
        return TokenBundle(
            accessToken = access,
            refreshToken = prefs.getString(KEY_REFRESH, null),
            idToken = prefs.getString(KEY_ID, null),
            expiresInSeconds = expires.takeIf { it >= 0 },
        )
    }

    actual fun savePendingPkce(codeVerifier: String, state: String) {
        prefs.edit()
            .putString(KEY_PKCE_VERIFIER, codeVerifier)
            .putString(KEY_PKCE_STATE, state)
            .apply()
    }

    actual fun readPendingPkce(): Pair<String, String>? {
        val verifier = prefs.getString(KEY_PKCE_VERIFIER, null) ?: return null
        val state = prefs.getString(KEY_PKCE_STATE, null) ?: return null
        return verifier to state
    }

    actual fun clearPendingPkce() {
        prefs.edit()
            .remove(KEY_PKCE_VERIFIER)
            .remove(KEY_PKCE_STATE)
            .apply()
    }

    actual fun clear() {
        prefs.edit().clear().apply()
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
