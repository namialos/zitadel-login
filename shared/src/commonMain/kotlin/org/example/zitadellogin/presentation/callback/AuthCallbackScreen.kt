package org.example.zitadellogin.presentation.callback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.zitadellogin.core.localization.FaStrings
import org.example.zitadellogin.core.ui.AppCenteredLoader
import org.example.zitadellogin.core.ui.AppErrorBanner
import org.example.zitadellogin.core.ui.AppPrimaryButton

@Composable
fun AuthCallbackRoute(
    viewModel: AuthCallbackViewModel,
    onSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startProcessing()
    }

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when {
                state.isLoading -> AppCenteredLoader(message = FaStrings.authCallbackLoading)
                state.errorMessage != null -> {
                    AppErrorBanner(message = state.errorMessage!!)
                    AppPrimaryButton(
                        text = FaStrings.retry,
                        onClick = onBackToLogin,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }
    }
}
