package org.example.zitadellogin.core.oauth

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import org.example.zitadellogin.AndroidContextHolder
import org.example.zitadellogin.core.logging.AppLogger

actual class SystemOAuthBrowser actual constructor() {
    actual suspend fun openAuthorizationUrl(url: String): String? {
        AppLogger.i("SystemOAuthBrowser.openAuthorizationUrl (Android Custom Tab)")
        AppLogger.dump("Custom Tab opening URL", url)
        val activity = AndroidContextHolder.requireActivity()
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(activity, Uri.parse(url))
        return null
    }
}
