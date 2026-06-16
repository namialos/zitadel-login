package org.example.zitadellogin.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onLoggedOut: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val logoutNav by viewModel.logoutDone.collectAsState()

    LaunchedEffect(logoutNav) {
        if (logoutNav && viewModel.consumeLogoutNavigation()) {
            onLoggedOut()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeBackground(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.24f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    HomeTopSection()
                }

                Box(
                    modifier = Modifier
                        .weight(0.52f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    HomeCenterSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        profile = state.profile,
                        errorMessage = state.errorMessage,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(0.24f)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    HomeLogoutButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.logout() },
                    )
                }
            }
        }
    }
}
