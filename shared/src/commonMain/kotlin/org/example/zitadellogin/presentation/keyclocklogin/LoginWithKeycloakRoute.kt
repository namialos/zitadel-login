package org.example.zitadellogin.presentation.keyclocklogin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiplatform.webview.jsbridge.*
import com.multiplatform.webview.web.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginWithKeycloakRoute(
    url: String,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    viewModel: LoginWithKeycloakViewModel = koinViewModel(),
    onBackPressed: () -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val webViewState = rememberWebViewState(url)
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge(navigator)

    DisposableEffect(Unit) {

        initWebView(webViewState)

        val handler = object : IJsMessageHandler {

            override fun methodName() = "logout"

            override fun handle(
                message: JsMessage,
                navigator: WebViewNavigator?,
                callback: (String) -> Unit
            ) {
                viewModel.logout(
                    onSuccess = onBackPressed
                )
            }
        }

        jsBridge.register(handler)

        onDispose {
            jsBridge.unregister(handler)
        }
    }

    when (val loading = webViewState.loadingState) {

        LoadingState.Initializing ->
            viewModel.onPageStarted()

        LoadingState.Finished ->
            viewModel.onPageFinished()

        is LoadingState.Loading ->
            viewModel.onProgressChanged(
                loading.progress
            )
    }

    LaunchedEffect(state.tokenScript) {
        state.tokenScript?.let {
            navigator.evaluateJavaScript(it)
            viewModel.consumeTokenScript()
        }
    }

    BackPressHandler {
        if (navigator.canGoBack) {
            navigator.navigateBack()
        } else {
            onBackPressed()
        }
    }

    LoginWithKeycloakScreen(
        webViewState = webViewState,
        webViewNavigator = navigator,
        jsBridge = jsBridge,
        backgroundColor = backgroundColor,
        uiState = state,
        onBack = {
            if (navigator.canGoBack) {
                navigator.navigateBack()
            } else {
                onBackPressed()
            }
        },
        onClose = onBackPressed
    )
}

@Composable
fun LoginWithKeycloakScreen(
    webViewState: WebViewState,
    webViewNavigator: WebViewNavigator,
    jsBridge: WebViewJsBridge,
    backgroundColor: Color,
    uiState: LoginWithKeycloakUiState,
    onBack: () -> Unit,
    onClose: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // Progress
        if (uiState.showProgress) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .zIndex(5f),
                progress = {
                    uiState.loadProgress
                },
                color = MaterialTheme.colorScheme.primary
            )
        }

        // WebView
        WebView(
            state = webViewState,
            navigator = webViewNavigator,
            webViewJsBridge = jsBridge,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
expect fun BackPressHandler(onBackPressed: () -> Unit)


fun initWebView(webViewState: WebViewState) {
    webViewState.webSettings.apply {
        zoomLevel = 1.0
        isJavaScriptEnabled = true
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = true
            allowFileAccess = true
            domStorageEnabled = true
            allowMidiSysexMessages = true
            allowFileAccess = true
            domStorageEnabled = true
            useWideViewPort = true
            loadsImagesAutomatically = true
            mediaPlaybackRequiresUserGesture = false
        }
    }
}