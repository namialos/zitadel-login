package org.example.zitadellogin.di

import org.example.zitadellogin.core.network.createAuthenticatedHttpClient
import org.example.zitadellogin.core.network.createJson
import org.example.zitadellogin.core.network.createPublicHttpClient
import org.example.zitadellogin.core.oauth.OAuthExternalDeepLinks
import org.example.zitadellogin.core.oauth.OAuthExternalDeepLinksImpl
import org.example.zitadellogin.core.oauth.OAuthNavigationBus
import org.example.zitadellogin.core.oauth.SystemOAuthBrowser
import org.example.zitadellogin.data.oauth.OAuthPkceSessionStore
import org.example.zitadellogin.data.remote.AuthRemoteDataSource
import org.example.zitadellogin.data.repository.AuthRepositoryImpl
import org.example.zitadellogin.data.token.TokenManager
import org.example.zitadellogin.domain.repository.AuthRepository
import org.example.zitadellogin.domain.usecase.BuildLoginAuthorizeUrlUseCase
import org.example.zitadellogin.domain.usecase.BuildSignupAuthorizeUrlUseCase
import org.example.zitadellogin.domain.usecase.CompleteAuthorizationCodeLoginUseCase
import org.example.zitadellogin.domain.usecase.FetchCurrentProfileUseCase
import org.example.zitadellogin.domain.usecase.LogoutUseCase
import org.example.zitadellogin.domain.usecase.ObserveAuthStateUseCase
import org.example.zitadellogin.domain.usecase.RestoreSessionFromStorageUseCase
import org.example.zitadellogin.presentation.callback.AuthCallbackViewModel
import org.example.zitadellogin.presentation.home.HomeViewModel
import org.example.zitadellogin.presentation.login.LoginViewModel
import org.example.zitadellogin.presentation.splash.SplashViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect fun platformNativeModule(): Module

fun sharedAppModule(): Module = module {
    single { createJson() }
    single(named("publicHttp")) { createPublicHttpClient(get()) }
    single(named("authHttp")) { createAuthenticatedHttpClient(get(), get()) }
    single { AuthRemoteDataSource(get(named("publicHttp")), get(named("authHttp")), get()) }

    single<OAuthExternalDeepLinks> { OAuthExternalDeepLinksImpl() }
    single { OAuthNavigationBus() }
    single { SystemOAuthBrowser() }
    singleOf(::OAuthPkceSessionStore)
    singleOf(::TokenManager)
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }

    factoryOf(::BuildLoginAuthorizeUrlUseCase)
    factoryOf(::BuildSignupAuthorizeUrlUseCase)
    factoryOf(::CompleteAuthorizationCodeLoginUseCase)
    factoryOf(::FetchCurrentProfileUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveAuthStateUseCase)
    factoryOf(::RestoreSessionFromStorageUseCase)

    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::AuthCallbackViewModel)
    viewModelOf(::HomeViewModel)
}

fun appModules(): List<Module> = listOf(platformNativeModule(), sharedAppModule())
