package org.example.zitadellogin.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.zitadellogin.core.localization.FaStrings
import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.core.oauth.OAuthNavigationBus
import org.example.zitadellogin.core.oauth.SystemOAuthBrowser
import org.example.zitadellogin.domain.usecase.BuildLoginAuthorizeUrlUseCase
import org.example.zitadellogin.domain.usecase.BuildSignupAuthorizeUrlUseCase

data class LoginUiState(
    val isBusy: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * Native OAuth login — no credentials collected in-app.
 * Login / signup buttons redirect to ZITADEL hosted pages via system browser.
 */
class LoginViewModel(
    private val buildLoginAuthorizeUrl: BuildLoginAuthorizeUrlUseCase,
    private val buildSignupAuthorizeUrl: BuildSignupAuthorizeUrlUseCase,
    private val systemOAuthBrowser: SystemOAuthBrowser,
    private val navigationBus: OAuthNavigationBus,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        navigationBus.errors
            .onEach { message ->
                _uiState.value = LoginUiState(isBusy = false, errorMessage = message)
            }
            .launchIn(viewModelScope)
    }

    fun onStartLogin() {
        openZitadelAuthFlow { buildLoginAuthorizeUrl() }
    }

    fun onStartRegistration() {
        openZitadelAuthFlow { buildSignupAuthorizeUrl() }
    }

    private fun openZitadelAuthFlow(urlBuilder: suspend () -> Result<String>) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isBusy = true, errorMessage = null)
            urlBuilder().fold(
                onSuccess = { url ->
                    AppLogger.i("Redirecting to ZITADEL login page")
                    AppLogger.dump("LoginViewModel authorize URL", url)
                    val redirectUrl = systemOAuthBrowser.openAuthorizationUrl(url)
                    if (redirectUrl != null) {
                        AppLogger.dump("LoginViewModel browser returned redirect", redirectUrl)
                        navigationBus.deliverRedirect(redirectUrl)
                    } else {
                        AppLogger.i("System browser opened — user authenticates on ZITADEL hosted page")
                    }
                    _uiState.value = LoginUiState(isBusy = false)
                },
                onFailure = { e ->
                    AppLogger.e("Failed to start ZITADEL redirect", e)
                    _uiState.value = LoginUiState(
                        isBusy = false,
                        errorMessage = e.message ?: FaStrings.errorGeneric,
                    )
                },
            )
        }
    }
}
