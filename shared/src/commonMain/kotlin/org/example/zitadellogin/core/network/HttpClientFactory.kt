package org.example.zitadellogin.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.zitadellogin.core.config.AuthConfig
import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.core.session.SessionTokenHolder

expect fun createPlatformHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient

fun createJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

private fun HttpClientConfig<*>.installCommon(json: Json) {
    install(ContentNegotiation) {
        json(json)
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
                AppLogger.d("Ktor: $message")
            }
        }
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 20_000
        socketTimeoutMillis = 20_000
    }
    install(HttpRequestRetry) {
        maxRetries = 2
        retryIf { _, response -> response.status.value in 500..599 }
        exponentialDelay()
    }
}

/** Backend + OIDC discovery/config (no bearer). */
fun createPublicHttpClient(json: Json): HttpClient = createPlatformHttpClient {
    installCommon(json)
    defaultRequest {
        url(AuthConfig.ISSUER)
    }
}

/** Authenticated backend API calls. */
fun createAuthenticatedHttpClient(
    json: Json,
    sessionTokenHolder: SessionTokenHolder,
): HttpClient = createPlatformHttpClient {
    installCommon(json)
    defaultRequest {
        url(AuthConfig.ISSUER)
        sessionTokenHolder.snapshot()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
