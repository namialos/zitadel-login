package org.example.zitadellogin.di

import org.example.zitadellogin.core.session.IosSessionTokenHolder
import org.example.zitadellogin.core.session.SessionTokenHolder
import org.example.zitadellogin.data.local.SecureAuthStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformNativeModule(): Module = module {
    single<SessionTokenHolder> { IosSessionTokenHolder() }
    single { SecureAuthStorage() }
}
