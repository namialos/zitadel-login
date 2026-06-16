package org.example.zitadellogin.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.core.oauth.PkceHelper
import org.example.zitadellogin.core.oauth.generateOAuthState
import org.example.zitadellogin.data.mapper.toDomain
import org.example.zitadellogin.data.oauth.OAuthPkceSessionStore
import org.example.zitadellogin.data.oauth.PendingOAuthSession
import org.example.zitadellogin.data.oauth.buildAuthorizationUrl
import org.example.zitadellogin.data.oauth.buildSignupAuthorizationUrl
import org.example.zitadellogin.data.remote.AuthRemoteDataSource
import org.example.zitadellogin.data.remote.TokenExchangeResult
import org.example.zitadellogin.data.remote.toUserMessage
import org.example.zitadellogin.data.token.TokenManager
import org.example.zitadellogin.domain.model.AuthState
import org.example.zitadellogin.domain.model.TokenBundle
import org.example.zitadellogin.domain.model.UserProfile
import org.example.zitadellogin.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remote: AuthRemoteDataSource,
    private val tokenManager: TokenManager,
    private val pkceSessionStore: OAuthPkceSessionStore,
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private fun beginPkceSession(): Pair<String, String> {
        val verifier = PkceHelper.generateVerifier()
        val challenge = PkceHelper.challengeForVerifier(verifier)
        val state = generateOAuthState()
        pkceSessionStore.begin(
            PendingOAuthSession(
                codeVerifier = verifier,
                state = state,
            ),
        )
        return challenge to state
    }

    override suspend fun buildLoginAuthorizeUrl(): Result<String> = runCatching {
        val (challenge, state) = beginPkceSession()
        buildAuthorizationUrl(codeChallenge = challenge, state = state).also { url ->
            AppLogger.dump("OAuth authorize URL (login)", url)
        }
    }

    override suspend fun buildSignupAuthorizeUrl(): Result<String> = runCatching {
        val (challenge, state) = beginPkceSession()
        buildSignupAuthorizationUrl(codeChallenge = challenge, state = state).also { url ->
            AppLogger.dump("OAuth authorize URL (signup)", url)
        }
    }

    override suspend fun completeLoginWithAuthorizationCode(code: String, state: String): Result<UserProfile> {
        AppLogger.dump(
            "AuthRepository.completeLogin input",
            "code=$code\nstate=$state",
        )

        val pending = pkceSessionStore.validate(state)
            ?: return Result.failure(IllegalStateException("جلسه PKCE یا state معتبر نیست."))

        val tokenResult = try {
            remote.exchangeAuthorizationCode(
                code = code,
                codeVerifier = pending.codeVerifier,
            )
        } finally {
            pkceSessionStore.clear()
        }

        return when (tokenResult) {
            is TokenExchangeResult.Success -> {
                AppLogger.dump(
                    "AuthRepository token exchange success",
                    "access_token=${tokenResult.accessToken}\n" +
                        "refresh_token=${tokenResult.dto.refreshToken}\n" +
                        "id_token=${tokenResult.dto.idToken}\n" +
                        "expires_in=${tokenResult.dto.expiresIn}",
                )
                tokenManager.persist(
                    TokenBundle(
                        accessToken = tokenResult.accessToken,
                        refreshToken = tokenResult.dto.refreshToken,
                        idToken = tokenResult.dto.idToken,
                        expiresInSeconds = tokenResult.dto.expiresIn,
                    ),
                )
                refreshProfile()
            }

            is TokenExchangeResult.OAuthError -> {
                AppLogger.dump(
                    "AuthRepository token exchange failed",
                    "error=${tokenResult.error}\nhttp=${tokenResult.httpStatus}\n" +
                        "description=${tokenResult.description}\nbody=${tokenResult.rawBody}",
                )
                Result.failure(IllegalStateException(tokenResult.toUserMessage()))
            }
        }
    }

    override suspend fun refreshProfile(): Result<UserProfile> {
        val profileResult = remote.unwrap {
            remote.whoami().toDomain()
                ?: throw IllegalStateException("ساختار پاسخ whoami شناخته‌شده نیست.")
        }
        profileResult.onSuccess { profile ->
            AppLogger.dump(
                "AuthRepository.refreshProfile success",
                "userId=${profile.userId}\nusername=${profile.username}\n" +
                    "displayName=${profile.displayName}\nemail=${profile.email}",
            )
            _authState.update { AuthState.Authenticated(profile) }
        }.onFailure { e ->
            AppLogger.e("AuthRepository.refreshProfile failed", e)
            _authState.update { AuthState.Unauthenticated }
        }
        return profileResult
    }

    override suspend fun logout(): Result<Unit> {
        AppLogger.i("AuthRepository.logout — clearing local tokens only")
        pkceSessionStore.clear()
        tokenManager.clear()
        _authState.update { AuthState.Unauthenticated }
        return Result.success(Unit)
    }

    override suspend fun restoreSessionFromStorage(): Boolean {
        val access = tokenManager.loadAccessToken()
        if (access.isNullOrBlank()) {
            _authState.update { AuthState.Unauthenticated }
            return false
        }
        val profile = refreshProfile()
        return if (profile.isSuccess) {
            true
        } else {
            val refresh = tokenManager.refreshAccessTokenIfPossible()
            if (refresh.isSuccess) {
                refreshProfile().isSuccess
            } else {
                AppLogger.w("نشست ذخیره‌شده نامعتبر است.", profile.exceptionOrNull())
                tokenManager.clear()
                _authState.update { AuthState.Unauthenticated }
                false
            }
        }
    }
}
