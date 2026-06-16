package org.example.zitadellogin

import android.app.Application
import org.example.zitadellogin.core.oauth.OAuthDeepLinkRegistry
import org.example.zitadellogin.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ZitadelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.init(this)
        val koinApp = startKoin {
            androidLogger()
            androidContext(this@ZitadelApplication)
            modules(appModules())
        }
        OAuthDeepLinkRegistry.externalDeepLinks = koinApp.koin.get()
    }
}
