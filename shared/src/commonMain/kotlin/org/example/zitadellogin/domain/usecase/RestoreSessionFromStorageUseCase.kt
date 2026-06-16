package org.example.zitadellogin.domain.usecase

import org.example.zitadellogin.domain.repository.AuthRepository

class RestoreSessionFromStorageUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Boolean = repository.restoreSessionFromStorage()
}
