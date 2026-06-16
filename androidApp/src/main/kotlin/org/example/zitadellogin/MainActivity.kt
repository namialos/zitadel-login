package org.example.zitadellogin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.example.zitadellogin.core.logging.AppLogger
import org.example.zitadellogin.core.oauth.OAuthDeepLinkRegistry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidContextHolder.bindActivity(this)
        AppLogger.i("MainActivity.onCreate")
        deliverOAuthDeepLink(intent)
        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        AppLogger.i("MainActivity.onNewIntent")
        deliverOAuthDeepLink(intent)
    }

    override fun onDestroy() {
        AndroidContextHolder.unbindActivity(this)
        super.onDestroy()
    }

    private fun deliverOAuthDeepLink(intent: Intent?) {
        val data = intent?.data?.toString() ?: return
        OAuthDeepLinkRegistry.handle(data)
    }
}
