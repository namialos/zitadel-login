package org.example.zitadellogin.core.oauth

import org.example.zitadellogin.core.logging.AppLogger

object OAuthDeepLinkRegistry {
    var externalDeepLinks: OAuthExternalDeepLinks? = null

    fun handle(url: String) {
        AppLogger.i("OAuthDeepLinkRegistry.handle")
        AppLogger.dump("MainActivity deep link", url)
        externalDeepLinks?.offer(url)
    }
}
