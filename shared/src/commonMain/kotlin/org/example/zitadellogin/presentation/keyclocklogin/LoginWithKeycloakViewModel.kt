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

class LoginWithKeycloakViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            LoginWithKeycloakUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    private var handledCode = false

    fun onPageStarted() {
        _uiState.value =
            _uiState.value.copy(
                showProgress = true
            )
    }

    fun onPageFinished() {
        _uiState.value =
            _uiState.value.copy(
                showProgress = false
            )
    }

    fun onProgressChanged(
        progress: Float
    ) {
        _uiState.value =
            _uiState.value.copy(
                loadProgress = progress
            )
    }

    fun onRedirect(
        url: String
    ) {

        if (
            handledCode ||
            !url.startsWith(
                "http://localhost:4202"
            )
        ) return

        val code =
            extractQueryParam(
                url,
                "code"
            ) ?: return

        handledCode = true

        exchangeCode(
            code
        )
    }

    private fun exchangeCode(
        code: String
    ) {

        viewModelScope.launch {

            println(
                "Authorization Code = $code"
            )

            // next step:
            // call token endpoint


        }
    }

   /* private fun sendAccessToken(
        accessToken: String
    ) {

        val json =
            """
            {
                "accessToken":"$accessToken"
            }
            """.trimIndent()

        val script =
            """
            document.dispatchEvent(
                new CustomEvent(
                    "mobile-auth",
                    {
                        detail:{
                            data:$json
                        }
                    }
                )
            );
            """.trimIndent()

        _uiState.value =
            _uiState.value.copy(
                tokenScript = script
            )
    }

    fun consumeTokenScript() {
        _uiState.value =
            _uiState.value.copy(
                tokenScript = null
            )
    }*/

    fun logout(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

        }
    }
}

private fun extractQueryParam(
    url: String,
    key: String
): String? {

    val query =
        url.substringAfter(
            "?",
            ""
        )

    return query
        .split("&")
        .firstNotNullOfOrNull {

            val pair =
                it.split("=")

            pair
                .takeIf {
                    it.size == 2 &&
                            it[0] == key
                }
                ?.get(1)
        }
}