package org.example.zitadellogin.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.zitadellogin.domain.usecase.RestoreSessionFromStorageUseCase

sealed interface SplashDestination {
    data object Login : SplashDestination
    data object Home : SplashDestination
}

class SplashViewModel(
    private val restoreSessionFromStorage: RestoreSessionFromStorageUseCase,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    fun start() {
        if (_destination.value != null) return
        viewModelScope.launch {
            delay(900)
            val restored = restoreSessionFromStorage()
            _destination.value = if (restored) SplashDestination.Home else SplashDestination.Login
        }
    }
}
