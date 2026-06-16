package org.example.zitadellogin.data.token

import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.core.session.SessionTokenHolder
import org.example.zitadellogin.data.local.SecureAuthStorage
import org.example.zitadellogin.data.remote.AuthRemoteDataSource
import org.example.zitadellogin.data.remote.TokenExchangeResult
import org.example.zitadellogin.data.remote.toUserMessage
import org.example.zitadellogin.domain.model.TokenBundle

class TokenManager(
    private val secureAuthStorage: SecureAuthStorage,
    private val sessionTokenHolder: SessionTokenHolder,
    private val remote: AuthRemoteDataSource,
) {
    fun persist(tokens: TokenBundle) {
        AppLogger.dump(
            "TokenManager.persist",
            "access_token=${tokens.accessToken}\nrefresh_token=${tokens.refreshToken}\n" +
                "id_token=${tokens.idToken}\nexpires_in=${tokens.expiresInSeconds}",
        )
        secureAuthStorage.saveTokens(tokens)
        sessionTokenHolder.setToken(tokens.accessToken)
    }

    fun loadAccessToken(): String? {
        val stored = secureAuthStorage.readTokens() ?: return null
        AppLogger.dump(
            "TokenManager.loadAccessToken",
            "access_token=${stored.accessToken}\nrefresh_token=${stored.refreshToken}",
        )
        sessionTokenHolder.setToken(stored.accessToken)
        return stored.accessToken
    }

    fun clear() {
        AppLogger.i("TokenManager.clear")
        secureAuthStorage.clear()
        sessionTokenHolder.setToken(null)
    }

    suspend fun refreshAccessTokenIfPossible(): Result<TokenBundle> {
        val current = secureAuthStorage.readTokens()
            ?: return Result.failure(IllegalStateException("توکن ذخیره‌شده‌ای وجود ندارد."))
        val refresh = current.refreshToken
            ?: return Result.failure(IllegalStateException("refresh_token در دسترس نیست."))

        return when (val result = remote.refreshToken(refresh)) {
            is TokenExchangeResult.Success -> {
                val bundle = TokenBundle(
                    accessToken = result.accessToken,
                    refreshToken = result.dto.refreshToken ?: current.refreshToken,
                    idToken = result.dto.idToken ?: current.idToken,
                    expiresInSeconds = result.dto.expiresIn,
                )
                AppLogger.dump(
                    "TokenManager.refresh success",
                    "access_token=${bundle.accessToken}\nrefresh_token=${bundle.refreshToken}",
                )
                persist(bundle)
                Result.success(bundle)
            }

            is TokenExchangeResult.OAuthError -> {
                AppLogger.dump(
                    "TokenManager.refresh failed",
                    "error=${result.error}\nbody=${result.rawBody}",
                )
                Result.failure(IllegalStateException(result.toUserMessage()))
            }
        }
    }
}
