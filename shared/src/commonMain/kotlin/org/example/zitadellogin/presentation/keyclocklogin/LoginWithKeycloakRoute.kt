package org.example.zitadellogin.presentation.keyclocklogin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import org.example.zitadellogin.presentation.keyclocklogin.utils.authUrl
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun LoginWithKeycloakRoute(
    url: String = authUrl,
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



    LaunchedEffect(
        webViewState.lastLoadedUrl
    ) {

        webViewState.lastLoadedUrl
            ?.let(
                viewModel::onRedirect
            )
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

/*    LaunchedEffect(state.tokenScript) {
        state.tokenScript?.let {
            navigator.evaluateJavaScript(it)
            viewModel.consumeTokenScript()
        }
    }*/

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
            .padding(top = 24.dp)
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Toolbar
        Box(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(24.dp).align(Alignment.CenterStart)
                    .clickable { onBack.invoke() },
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(24.dp).align(Alignment.CenterEnd)
                    .clickable { onClose.invoke() },
            )

            Text(
                text = "KeyClock",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )


        }

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