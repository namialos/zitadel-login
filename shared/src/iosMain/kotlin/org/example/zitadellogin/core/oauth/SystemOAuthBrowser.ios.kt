package org.example.zitadellogin.core.oauth

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.zitadellogin.core.config.AuthConfig
import org.example.zitadellogin.core.logging.AppLogger
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
private class OAuthPresentationContext : NSObject(), ASWebAuthenticationPresentationContextProvidingProtocol {
    override fun presentationAnchorForWebAuthenticationSession(session: ASWebAuthenticationSession): ASPresentationAnchor {
        val app = UIApplication.sharedApplication
        @Suppress("UNCHECKED_CAST")
        val windows = app.windows as? List<UIWindow>
        return windows?.firstOrNull { it.isKeyWindow() }
            ?: windows?.firstOrNull()
            ?: error("No UIWindow available for ASWebAuthenticationSession")
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class SystemOAuthBrowser actual constructor() {
    private var activeSession: ASWebAuthenticationSession? = null

    actual suspend fun openAuthorizationUrl(url: String): String? =
        suspendCancellableCoroutine { cont ->
            AppLogger.i("SystemOAuthBrowser.openAuthorizationUrl (iOS ASWebAuthenticationSession)")
            AppLogger.dump("ASWebAuthenticationSession opening URL", url)
            val nsUrl = NSURL.URLWithString(url)
            if (nsUrl == null) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            val session = ASWebAuthenticationSession(
                uRL = nsUrl,
                callbackURLScheme = AuthConfig.REDIRECT_SCHEME,
                completionHandler = { callbackUrl, error ->
                    activeSession = null
                    when {
                        error != null -> {
                            AppLogger.e("ASWebAuthenticationSession error: ${error.localizedDescription}")
                            cont.resume(null)
                        }
                        callbackUrl != null -> {
                            val redirect = callbackUrl.absoluteString
                            AppLogger.dump("ASWebAuthenticationSession callback", redirect)
                            cont.resume(redirect)
                        }
                        else -> {
                            AppLogger.w("ASWebAuthenticationSession cancelled or empty callback")
                            cont.resume(null)
                        }
                    }
                },
            )
            session.presentationContextProvider = OAuthPresentationContext()
            session.prefersEphemeralWebBrowserSession = true
            activeSession = session

            if (!session.start()) {
                activeSession = null
                cont.resume(null)
            }

            cont.invokeOnCancellation {
                activeSession?.cancel()
                activeSession = null
            }
        }
}
