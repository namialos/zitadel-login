package org.example.zitadellogin.presentation.keyclocklogin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginWithKeycloakUiState(
    val showProgress: Boolean = false,
    val loadProgress: Float = 0f,
    val tokenScript: String? = null
)

class LoginWithKeycloakViewModel(
    private val dispatcherProvider: Dispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginWithKeycloakUiState())
    val uiState: StateFlow<LoginWithKeycloakUiState> = _uiState.asStateFlow()

    private var isTokenSent = false

    fun onPageStarted() {
        _uiState.value = _uiState.value.copy(
            showProgress = true
        )
    }

    fun onPageFinished() {
        _uiState.value = _uiState.value.copy(
            showProgress = false
        )

        if (!isTokenSent) {
            sendAccessToken()
            isTokenSent = true
        }
    }

    fun onProgressChanged(progress: Float) {
        _uiState.value = _uiState.value.copy(
            loadProgress = progress
        )
    }

    private fun sendAccessToken() {
        viewModelScope.launch(dispatcherProvider.IO) {
            delay(500)

            val json = ""

            val script = """
                document.dispatchEvent(
                    new CustomEvent(
                        "mobile-auth",
                        {
                            detail: {
                                data: '$json'
                            }
                        }
                    )
                );
            """.trimIndent()

            _uiState.value = _uiState.value.copy(
                tokenScript = script
            )
        }
    }

    fun consumeTokenScript() {
        _uiState.value = _uiState.value.copy(
            tokenScript = null
        )
    }

    fun logout(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

        }
    }
}