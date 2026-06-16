package org.example.zitadellogin.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android

actual fun createPlatformHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(Android) {
        engine {
            connectTimeout = 20_000
            socketTimeout = 20_000
        }
        block()
    }
