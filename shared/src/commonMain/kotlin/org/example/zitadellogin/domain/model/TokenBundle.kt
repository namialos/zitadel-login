package org.example.zitadellogin.domain.model

data class TokenBundle(
    val accessToken: String,
    val refreshToken: String?,
    val idToken: String?,
    val expiresInSeconds: Long?,
)
