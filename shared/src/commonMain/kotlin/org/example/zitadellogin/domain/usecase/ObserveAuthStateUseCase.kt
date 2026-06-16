package org.example.zitadellogin.domain.usecase

import org.example.zitadellogin.domain.repository.AuthRepository

class ObserveAuthStateUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.authState
}
