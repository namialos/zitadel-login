package org.example.zitadellogin.core.oauth

import org.example.zitadellogin.core.logging.AppLogger

/**
 * Short-lived holder between deep-link delivery and [presentation.callback.AuthCallbackRoute].
 */
object OAuthPendingCallback {
    private var payload: OAuthCallbackPayload? = null

    fun set(payload: OAuthCallbackPayload) {
        AppLogger.dump(
            "OAuthPendingCallback.set",
            "code=${payload.code}\nstate=${payload.state}",
        )
        this.payload = payload
    }

    fun consume(): OAuthCallbackPayload? {
        val current = payload
        payload = null
        if (current != null) {
            AppLogger.dump(
                "OAuthPendingCallback.consume",
                "code=${current.code}\nstate=${current.state}",
            )
        } else {
            AppLogger.w("OAuthPendingCallback.consume -> null (no pending payload)")
        }
        return current
    }
}
