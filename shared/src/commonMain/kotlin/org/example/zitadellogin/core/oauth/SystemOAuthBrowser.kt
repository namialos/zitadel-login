package org.example.zitadellogin.core.oauth

/**
 * Opens the system browser for OIDC (Custom Tabs on Android, ASWebAuthenticationSession on iOS).
 */
expect class SystemOAuthBrowser() {
  /**
   * @return redirect URL on iOS when [ASWebAuthenticationSession] completes; null on Android (deep link completes flow).
   */
    suspend fun openAuthorizationUrl(url: String): String?
}
