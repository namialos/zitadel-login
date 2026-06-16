package org.example.zitadellogin.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.example.zitadellogin.core.config.AuthConfig
import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.data.dto.OAuthErrorDto
import org.example.zitadellogin.data.dto.TokenResponseDto
import org.example.zitadellogin.data.dto.WhoAmiDto

class AuthRemoteDataSource(
    private val publicClient: HttpClient,
    private val authenticatedClient: HttpClient,
    private val json: Json,
) {
    suspend fun exchangeAuthorizationCode(
        code: String,
        codeVerifier: String,
    ): TokenExchangeResult {
        val tokenUrl = AuthConfig.tokenEndpoint()

        val formBody = buildString {
            append("grant_type=authorization_code")
            append("&code=").append(code)
            append("&code_verifier=").append(codeVerifier)
            append("&redirect_uri=").append(AuthConfig.REDIRECT_URI)
            append("&client_id=").append(AuthConfig.CLIENT_ID)
        }
        AppLogger.i(">>> REQUEST OAuth token POST $tokenUrl")
        AppLogger.dump("OAuth token request body", formBody)

        val response = publicClient.post(tokenUrl) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("grant_type", "authorization_code")
                        append("code", code)
                        append("code_verifier", codeVerifier)
                        append("redirect_uri", AuthConfig.REDIRECT_URI)
                        append("client_id", AuthConfig.CLIENT_ID)
                    },
                ),
            )
        }

        val rawBody = response.bodyAsText()
        logHttpResponse("OAuth token", response, rawBody)
        return parseTokenResponse(response.status.value, rawBody)
    }

    suspend fun refreshToken(refreshToken: String): TokenExchangeResult {
        val tokenUrl = AuthConfig.tokenEndpoint()

        AppLogger.i(">>> REQUEST OAuth refresh POST $tokenUrl")
        AppLogger.dump(
            "OAuth refresh request body",
            "grant_type=refresh_token&refresh_token=$refreshToken&client_id=${AuthConfig.CLIENT_ID}",
        )

        val response = publicClient.post(tokenUrl) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("grant_type", "refresh_token")
                        append("refresh_token", refreshToken)
                        append("client_id", AuthConfig.CLIENT_ID)
                    },
                ),
            )
        }

        val rawBody = response.bodyAsText()
        logHttpResponse("OAuth refresh", response, rawBody)
        return parseTokenResponse(response.status.value, rawBody)
    }

    suspend fun whoami(): WhoAmiDto {
        val url = AuthConfig.whoamiEndpoint()
        AppLogger.i(">>> REQUEST whoami GET $url")
        val response = authenticatedClient.get(url)
        val rawBody = response.bodyAsText()
        logHttpResponse("whoami", response, rawBody)
        val dto = json.decodeFromString<WhoAmiDto>(rawBody)
        AppLogger.dump(
            "whoami parsed",
            "sub=${dto.sub}\nuser_id=${dto.user_id}\nuserId=${dto.userIdCamel}\n" +
                "preferred_username=${dto.preferred_username}\nusername=${dto.username}\n" +
                "name=${dto.name}\nemail=${dto.email}\nroles=${dto.roles}\n" +
                "endpoint_access=${dto.endpointAccess}",
        )
        return dto
    }

    suspend fun <T> unwrap(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: ResponseException) {
            val errorBody = runCatching { e.response.bodyAsText() }.getOrDefault("(no body)")
            AppLogger.e("HTTP ${e.response.status.value}")
            AppLogger.dump("HTTP error response", errorBody)
            Result.failure(mapHttpError(e))
        } catch (e: Throwable) {
            AppLogger.e("Network failure", e)
            Result.failure(e)
        }

    private fun logHttpResponse(label: String, response: HttpResponse, rawBody: String) {
        AppLogger.i("<<< RESPONSE $label status=${response.status.value}")
        AppLogger.dump("$label raw body", rawBody)
    }

    private fun parseTokenResponse(httpStatus: Int, rawBody: String): TokenExchangeResult {
        if (rawBody.isBlank()) {
            AppLogger.w("Token response empty httpStatus=$httpStatus")
            return TokenExchangeResult.OAuthError(
                error = "empty_response",
                description = "پاسخ خالی از سرور توکن دریافت شد.",
                httpStatus = httpStatus,
                rawBody = rawBody,
            )
        }

        val trimmed = rawBody.trim()

        if (trimmed.startsWith("{")) {
            val oauthError = runCatching { json.decodeFromString<OAuthErrorDto>(trimmed) }.getOrNull()
            if (oauthError?.error != null) {
                AppLogger.dump(
                    "OAuth token error parsed",
                    "error=${oauthError.error}\nerror_description=${oauthError.errorDescription}\nerror_uri=${oauthError.errorUri}",
                )
                return TokenExchangeResult.OAuthError(
                    error = oauthError.error,
                    description = oauthError.errorDescription,
                    httpStatus = httpStatus,
                    rawBody = rawBody,
                )
            }

            val dto = runCatching { json.decodeFromString<TokenResponseDto>(trimmed) }.getOrNull()
            val access = dto?.resolvedAccessToken()
            if (access != null) {
                AppLogger.dump(
                    "OAuth token success parsed",
                    "access_token=$access\nrefresh_token=${dto.refreshToken}\nid_token=${dto.idToken}\n" +
                        "expires_in=${dto.expiresIn}\ntoken_type=${dto.tokenType}",
                )
                return TokenExchangeResult.Success(accessToken = access, dto = dto)
            }

            AppLogger.w("Token JSON missing access_token")
            AppLogger.dump("Token JSON raw", trimmed)
            return TokenExchangeResult.OAuthError(
                error = "invalid_response",
                description = "access_token در JSON پاسخ یافت نشد.",
                httpStatus = httpStatus,
                rawBody = rawBody,
            )
        }

        if (trimmed.contains("access_token=")) {
            val params = parseFormBody(trimmed)
            AppLogger.dump(
                "OAuth token form-encoded parsed",
                params.entries.joinToString("\n") { "${it.key}=${it.value}" },
            )
            val access = params["access_token"]
            if (!access.isNullOrBlank()) {
                val dto = TokenResponseDto(
                    accessToken = access,
                    refreshToken = params["refresh_token"],
                    idToken = params["id_token"],
                    expiresIn = params["expires_in"]?.toLongOrNull(),
                    tokenType = params["token_type"],
                )
                return TokenExchangeResult.Success(accessToken = access, dto = dto)
            }
            val error = params["error"]
            if (error != null) {
                return TokenExchangeResult.OAuthError(
                    error = error,
                    description = params["error_description"],
                    httpStatus = httpStatus,
                    rawBody = rawBody,
                )
            }
        }

        if (!HttpStatusCode.fromValue(httpStatus).isSuccess()) {
            return TokenExchangeResult.OAuthError(
                error = "http_$httpStatus",
                description = trimmed.take(200),
                httpStatus = httpStatus,
                rawBody = rawBody,
            )
        }

        return TokenExchangeResult.OAuthError(
            error = "unparseable_response",
            description = "فرمت پاسخ توکن شناخته نشد.",
            httpStatus = httpStatus,
            rawBody = rawBody,
        )
    }

    private fun parseFormBody(body: String): Map<String, String> =
        body.split('&').mapNotNull { part ->
            val idx = part.indexOf('=')
            if (idx == -1) null
            else part.substring(0, idx) to part.substring(idx + 1)
        }.toMap()

    private fun mapHttpError(e: ResponseException): Throwable {
        val code = e.response.status.value
        val message = when (code) {
            400 -> "درخواست نامعتبر است. لطفاً دوباره وارد شوید."
            401 -> "احراز هویت ناموفق بود."
            403 -> "دسترسی مجاز نیست."
            in 500..599 -> "خطای سرور. بعداً تلاش کنید."
            else -> "خطای شبکه (${code})."
        }
        return IllegalStateException(message, e)
    }
}

fun TokenExchangeResult.toUserMessage(): String = when (this) {
    is TokenExchangeResult.Success -> ""
    is TokenExchangeResult.OAuthError -> when (error) {
        "invalid_grant" -> description?.let { "کد ورود نامعتبر است: $it" }
            ?: "کد ورود منقضی شده یا قبلاً استفاده شده است."

        "invalid_client" -> "شناسه کلاینت در ZITADEL پیکربندی نشده است."

        "invalid_request" -> description?.let { "درخواست OAuth نامعتبر: $it" }
            ?: "درخواست OAuth نامعتبر است."

        "unauthorized_client" -> "این کلاینت مجاز به دریافت توکن نیست."

        "access_denied" -> "دسترسی رد شد."

        "invalid_response", "unparseable_response", "empty_response" ->
            description ?: "پاسخ سرور توکن نامعتبر بود."

        else -> description ?: "خطای OAuth: $error"
    }
}
