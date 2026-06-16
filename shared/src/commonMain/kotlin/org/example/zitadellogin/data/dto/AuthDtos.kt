package org.example.zitadellogin.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthErrorDto(
    val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null,
    @SerialName("error_uri") val errorUri: String? = null,
)

@Serializable
data class TokenResponseDto(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("id_token") val idToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long? = null,
    @SerialName("token_type") val tokenType: String? = null,
) {
    fun resolvedAccessToken(): String? = accessToken?.takeIf { it.isNotBlank() }
}

@Serializable
data class WhoAmiDto(
    @SerialName("endpoint_access") val endpointAccess: String? = null,
    val sub: String? = null,
    val user_id: String? = null,
    @SerialName("userId") val userIdCamel: String? = null,
    val preferred_username: String? = null,
    val username: String? = null,
    val name: String? = null,
    val email: String? = null,
    val roles: List<String>? = null,
)
