package org.example.zitadellogin.domain.usecase

import org.example.zitadellogin.domain.model.UserProfile
import org.example.zitadellogin.domain.repository.AuthRepository

class FetchCurrentProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<UserProfile> = repository.refreshProfile()
}
