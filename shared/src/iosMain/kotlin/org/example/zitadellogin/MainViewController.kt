package org.example.zitadellogin

import androidx.compose.ui.window.ComposeUIViewController
import org.example.zitadellogin.core.oauth.OAuthDeepLinkRegistry
import org.example.zitadellogin.di.appModules
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.mp.KoinPlatform.getKoin

private var koinBootstrapped = false

fun MainViewController() = ComposeUIViewController {
    if (!koinBootstrapped) {
        try {
            startKoin { modules(appModules()) }
        } catch (_: KoinApplicationAlreadyStartedException) {
        }
        koinBootstrapped = true
        val koin = getKoin()
        OAuthDeepLinkRegistry.externalDeepLinks = koin.get()
    }
    App()
}
