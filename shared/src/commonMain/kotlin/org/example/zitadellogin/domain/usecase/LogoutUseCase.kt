package org.example.zitadellogin.domain.usecase

import org.example.zitadellogin.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
