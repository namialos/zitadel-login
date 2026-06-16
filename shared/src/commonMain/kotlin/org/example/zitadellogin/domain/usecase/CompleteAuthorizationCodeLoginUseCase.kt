package org.example.zitadellogin.domain.usecase

import org.example.zitadellogin.domain.model.UserProfile
import org.example.zitadellogin.domain.repository.AuthRepository

class CompleteAuthorizationCodeLoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(code: String, state: String): Result<UserProfile> =
        repository.completeLoginWithAuthorizationCode(code, state)
}
