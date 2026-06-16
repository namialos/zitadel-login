package org.example.zitadellogin.data.oauth

import org.example.zitadellogin.core.config.AuthConfig

fun buildAuthorizationUrl(
    codeChallenge: String,
    state: String,
    prompt: String? = null,
): String = buildString {
    append(AuthConfig.authorizationEndpoint())
    append("?response_type=code")
    append("&client_id=").append(encodeQuery(AuthConfig.CLIENT_ID))
    append("&redirect_uri=").append(encodeQuery(AuthConfig.REDIRECT_URI))
    append("&scope=").append(encodeQuery(AuthConfig.OIDC_SCOPES))
    append("&state=").append(encodeQuery(state))
    append("&code_challenge=").append(encodeQuery(codeChallenge))
    append("&code_challenge_method=S256")
    append("&prompt=login")
    if (prompt != null) {
        append("&prompt=").append(encodeQuery(prompt))
    }
}

fun buildSignupAuthorizationUrl(
    codeChallenge: String,
    state: String,
): String = buildAuthorizationUrl(
    codeChallenge = codeChallenge,
    state = state,
    prompt = "create",
)

private fun encodeQuery(value: String): String =
    value.encodeToByteArray().joinToString("") { b ->
        val c = b.toInt() and 0xFF
        when {
            c in 'a'.code..'z'.code ||
                c in 'A'.code..'Z'.code ||
                c in '0'.code..'9'.code ||
                c == '-'.code || c == '.'.code || c == '_'.code || c == '~'.code ->
                c.toChar().toString()

            else -> "%${c.toString(16).uppercase().padStart(2, '0')}"
        }
    }
