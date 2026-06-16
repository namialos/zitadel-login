package org.example.zitadellogin.data.remote

sealed class TokenExchangeResult {
    data class Success(val accessToken: String, val dto: org.example.zitadellogin.data.dto.TokenResponseDto) :
        TokenExchangeResult()

    data class OAuthError(
        val error: String,
        val description: String?,
        val httpStatus: Int,
        val rawBody: String,
    ) : TokenExchangeResult()
}
