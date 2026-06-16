package org.example.zitadellogin.domain.model

data class UserProfile(
    val userId: String,
    val username: String?,
    val displayName: String?,
    val email: String?,
)
