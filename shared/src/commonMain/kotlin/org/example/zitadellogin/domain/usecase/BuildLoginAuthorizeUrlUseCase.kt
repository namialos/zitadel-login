package org.example.zitadellogin.domain.usecase

import org.example.zitadellogin.domain.repository.AuthRepository

class BuildLoginAuthorizeUrlUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<String> = repository.buildLoginAuthorizeUrl()
}
