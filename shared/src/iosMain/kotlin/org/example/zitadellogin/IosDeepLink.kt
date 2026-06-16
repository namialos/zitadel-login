package org.example.zitadellogin

import org.example.zitadellogin.core.oauth.OAuthDeepLinkRegistry

fun onDeepLinkUrl(url: String) {
    OAuthDeepLinkRegistry.handle(url)
}
