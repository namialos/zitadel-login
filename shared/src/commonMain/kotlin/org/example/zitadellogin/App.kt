package org.example.zitadellogin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.zitadellogin.core.theme.ZitadelLoginTheme
import org.example.zitadellogin.presentation.root.RtlApplication
import org.example.zitadellogin.presentation.root.ZitadelNavHost

@Composable
fun App() {
    ZitadelLoginTheme {
        RtlApplication {
            Surface(modifier = Modifier.fillMaxSize()) {
                ZitadelNavHost()
            }
        }
    }
}
