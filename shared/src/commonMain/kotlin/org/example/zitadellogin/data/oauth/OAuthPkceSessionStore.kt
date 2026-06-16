package org.example.zitadellogin.data.oauth

import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.data.local.SecureAuthStorage

data class PendingOAuthSession(
    val codeVerifier: String,
    val state: String,
)

/**
 * Holds PKCE verifier + state between opening the system browser and receiving the deep link.
 * Persisted securely so the session survives process death while Custom Tabs is open.
 */
class OAuthPkceSessionStore(
    private val secureStorage: SecureAuthStorage,
) {
    fun begin(session: PendingOAuthSession) {
        secureStorage.savePendingPkce(session.codeVerifier, session.state)
        AppLogger.dump(
            "PKCE session stored",
            "state=${session.state}\ncode_verifier=${session.codeVerifier}",
        )
    }

    /** Validates state and returns session without clearing (clear after token exchange attempt). */
    fun validate(expectedState: String): PendingOAuthSession? {
        val stored = secureStorage.readPendingPkce() ?: run {
            AppLogger.w("PKCE session missing from secure storage")
            return null
        }
        val (verifier, state) = stored
        AppLogger.dump(
            "PKCE session read from storage",
            "stored_state=$state\nreturned_state=$expectedState\ncode_verifier=$verifier",
        )
        if (state != expectedState) {
            AppLogger.w("PKCE state mismatch")
            return null
        }
        AppLogger.i("PKCE state validated OK")
        return PendingOAuthSession(codeVerifier = verifier, state = state)
    }

    fun clear() {
        AppLogger.i("PKCE session cleared")
        secureStorage.clearPendingPkce()
    }
}
