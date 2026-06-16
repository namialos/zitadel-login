package org.example.zitadellogin.core.config

/**
 * Non-auth application configuration.
 * OIDC settings live in [AuthConfig].
 */
object AppConfig {
    const val SECURE_PREFS_FILE = "zitadel_secure_store"
    const val KEYCHAIN_SERVICE = "org.example.zitadellogin.auth"
}
