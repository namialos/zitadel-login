package org.example.zitadellogin.presentation.callback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.core.oauth.OAuthPendingCallback
import org.example.zitadellogin.domain.usecase.CompleteAuthorizationCodeLoginUseCase

data class AuthCallbackUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val success: Boolean = false,
)

class AuthCallbackViewModel(
    private val completeAuthorizationCodeLogin: CompleteAuthorizationCodeLoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthCallbackUiState())
    val uiState: StateFlow<AuthCallbackUiState> = _uiState.asStateFlow()

    private var hasStarted = false

    fun startProcessing() {
        if (hasStarted) return
        hasStarted = true
        AppLogger.i("AuthCallbackViewModel.startProcessing")
        val payload = OAuthPendingCallback.consume()
        if (payload == null) {
            AppLogger.w("AuthCallbackViewModel: no pending payload")
            _uiState.value = AuthCallbackUiState(
                isLoading = false,
                errorMessage = "اطلاعات بازگشت از احراز هویت یافت نشد.",
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthCallbackUiState(isLoading = true)
            AppLogger.dump(
                "AuthCallbackViewModel calling completeLogin",
                "code=${payload.code}\nstate=${payload.state}",
            )
            val result = completeAuthorizationCodeLogin(payload.code, payload.state)
            result.fold(
                onSuccess = { profile ->
                    AppLogger.dump(
                        "AuthCallbackViewModel login success",
                        "userId=${profile.userId}\nusername=${profile.username}\n" +
                            "displayName=${profile.displayName}\nemail=${profile.email}",
                    )
                    _uiState.value = AuthCallbackUiState(isLoading = false, success = true)
                },
                onFailure = { e ->
                    AppLogger.e("AuthCallbackViewModel login failed: ${e.message}", e)
                    _uiState.value = AuthCallbackUiState(
                        isLoading = false,
                        errorMessage = e.message ?: "ورود تکمیل نشد.",
                    )
                },
            )
        }
    }
}
