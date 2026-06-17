package org.example.zitadellogin.presentation.keyclocklogin

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
actual fun BackPressHandler(onBackPressed: () -> Unit) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    DisposableEffect(backDispatcher) {
        backDispatcher?.addCallback(callback)
        onDispose { callback.remove() }
    }
}