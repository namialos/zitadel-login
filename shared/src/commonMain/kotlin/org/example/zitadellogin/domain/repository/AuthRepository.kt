package org.example.zitadellogin.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.zitadellogin.domain.model.AuthState
import org.example.zitadellogin.domain.model.UserProfile

interface AuthRepository {
    val authState: Flow<AuthState>

    /** Builds authorize URL and opens ZITADEL login in system browser (Custom Tab / ASWebAuthenticationSession). */
    suspend fun buildLoginAuthorizeUrl(): Result<String>

    /** Builds authorize URL with signup prompt for ZITADEL hosted registration. */
    suspend fun buildSignupAuthorizeUrl(): Result<String>

    /** Called only after deep-link redirect — exchanges code for tokens, then fetches profile. */
    suspend fun completeLoginWithAuthorizationCode(code: String, state: String): Result<UserProfile>

    suspend fun refreshProfile(): Result<UserProfile>

    suspend fun restoreSessionFromStorage(): Boolean

    /** Local logout only — clears tokens from secure storage; does not call /backend/oauth/logout. */
    suspend fun logout(): Result<Unit>
}
