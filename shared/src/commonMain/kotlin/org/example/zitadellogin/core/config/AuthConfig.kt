package org.example.zitadellogin.core.config

/**
 * Single source of truth for OIDC / ZITADEL native authentication.
 */
object AuthConfig {
    const val ISSUER = "https://zitadel.pxteam.ir"
    const val API_BASE_URL = "$ISSUER/backend"

    /** Official ZITADEL native client ID — used for authorize and token endpoints. */
    const val CLIENT_ID = "373632460690310718"

    /**
     * Register exactly this redirect URI in ZITADEL for client [CLIENT_ID].
     */
    const val REDIRECT_URI = "org.example.zitadellogin://oauth/callback"

    /** URL scheme without path (ASWebAuthenticationSession on iOS). */
    const val REDIRECT_SCHEME = "org.example.zitadellogin"

    const val OIDC_SCOPES = "openid profile email offline_access"

    const val OIDC_AUTHORIZE_PATH = "/oauth/v2/authorize"
    const val OIDC_TOKEN_PATH = "/oauth/v2/token"
    const val WHOAMI_PATH = "/backend/api/whoami"

    /** Native app OAuth endpoints — hardcoded, never fetched from /backend/oauth/config. */
    fun authorizationEndpoint(): String = "${ISSUER.trimEnd('/')}$OIDC_AUTHORIZE_PATH"

    fun tokenEndpoint(): String = "${ISSUER.trimEnd('/')}$OIDC_TOKEN_PATH"

    fun whoamiEndpoint(): String = "${ISSUER.trimEnd('/')}$WHOAMI_PATH"
}
