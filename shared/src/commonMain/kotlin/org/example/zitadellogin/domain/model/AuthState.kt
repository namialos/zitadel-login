package org.example.zitadellogin.domain.model

sealed class AuthState {
    data object Unknown : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val profile: UserProfile) : AuthState()
}
