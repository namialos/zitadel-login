package org.example.zitadellogin.core.oauth

import io.ktor.http.decodeURLQueryComponent
import org.example.zitadellogin.core.config.AuthConfig
import org.example.zitadellogin.core.logging.AppLogger

data class OAuthCallbackPayload(
    val code: String,
    val state: String,
)

sealed class OAuthRedirectParseResult {
    data class Success(val payload: OAuthCallbackPayload) : OAuthRedirectParseResult()
    data class Error(val messageFa: String) : OAuthRedirectParseResult()
    data object Ignored : OAuthRedirectParseResult()
}

object OAuthDeepLinkHandler {
    fun parseRedirect(url: String): OAuthRedirectParseResult {
        AppLogger.i(">>> CALLBACK deep link received")
        AppLogger.dump("deep link raw url", url)

        if (!url.startsWith(AuthConfig.REDIRECT_URI) &&
            !url.startsWith("${AuthConfig.REDIRECT_SCHEME}://")
        ) {
            AppLogger.i("deep link ignored (not OAuth redirect)")
            return OAuthRedirectParseResult.Ignored
        }

        val query = url.substringAfter('?', missingDelimiterValue = "")
        if (query.isEmpty()) {
            AppLogger.w("deep link missing query string")
            return OAuthRedirectParseResult.Error("پاسخ احراز هویت نامعتبر است.")
        }

        val params = query.split('&').associate { part ->
            val idx = part.indexOf('=')
            if (idx == -1) part to "" else part.substring(0, idx) to part.substring(idx + 1)
        }
        AppLogger.dump(
            "deep link query params (raw)",
            params.entries.joinToString("\n") { "${it.key}=${it.value}" },
        )

        params["error"]?.let { oauthError ->
            val desc = decodeParam(params["error_description"]) ?: oauthError
            AppLogger.dump(
                "deep link OAuth error",
                "error=${decodeParam(oauthError)}\nerror_description=$desc",
            )
            return when (decodeParam(oauthError)) {
                "access_denied" -> OAuthRedirectParseResult.Error("ورود لغو شد.")
                else -> OAuthRedirectParseResult.Error("خطای احراز هویت: $desc")
            }
        }

        val code = decodeParam(params["code"])
            ?: return OAuthRedirectParseResult.Error("کد احراز هویت دریافت نشد.").also {
                AppLogger.w("deep link missing code param")
            }
        val state = decodeParam(params["state"])
            ?: return OAuthRedirectParseResult.Error("پارامتر state دریافت نشد.").also {
                AppLogger.w("deep link missing state param")
            }

        AppLogger.dump(
            "deep link parsed payload",
            "code=$code\nstate=$state\nredirect_uri=${AuthConfig.REDIRECT_URI}",
        )
        return OAuthRedirectParseResult.Success(OAuthCallbackPayload(code, state))
    }

    private fun decodeParam(raw: String?): String? =
        raw?.takeIf { it.isNotBlank() }?.decodeURLQueryComponent(plusIsSpace = false)
}
