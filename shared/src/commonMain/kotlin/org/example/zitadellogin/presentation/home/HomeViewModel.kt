package org.example.zitadellogin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.zitadellogin.domain.model.AuthState
import org.example.zitadellogin.domain.model.UserProfile
import org.example.zitadellogin.domain.usecase.LogoutUseCase
import org.example.zitadellogin.domain.usecase.ObserveAuthStateUseCase

data class HomeUiState(
    val profile: UserProfile? = null,
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null,
)

class HomeViewModel(
    observeAuthState: ObserveAuthStateUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = observeAuthState()
        .map { state ->
            when (state) {
                is AuthState.Authenticated -> HomeUiState(profile = state.profile)
                AuthState.Unknown -> HomeUiState(profile = null, errorMessage = null)
                AuthState.Unauthenticated -> HomeUiState(profile = null, errorMessage = "نشست معتبر نیست.")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    private val _logoutDone = MutableStateFlow(false)
    val logoutDone: StateFlow<Boolean> = _logoutDone

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _logoutDone.value = true
        }
    }

    fun consumeLogoutNavigation(): Boolean {
        if (!_logoutDone.value) return false
        _logoutDone.value = false
        return true
    }
}
