package org.example.zitadellogin.core.oauth

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.example.zitadellogin.core.logging.AppLogger

class OAuthNavigationBus {
    private val _callback = MutableSharedFlow<OAuthCallbackPayload>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val _errors = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val callback: SharedFlow<OAuthCallbackPayload> = _callback.asSharedFlow()
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    fun deliverRedirect(url: String) {
        AppLogger.i("OAuthNavigationBus.deliverRedirect")
        when (val result = OAuthDeepLinkHandler.parseRedirect(url)) {
            is OAuthRedirectParseResult.Success -> {
                AppLogger.dump(
                    "OAuthNavigationBus callback payload",
                    "code=${result.payload.code}\nstate=${result.payload.state}",
                )
                _callback.tryEmit(result.payload)
            }
            is OAuthRedirectParseResult.Error -> {
                AppLogger.w("OAuthNavigationBus error: ${result.messageFa}")
                _errors.tryEmit(result.messageFa)
            }
            OAuthRedirectParseResult.Ignored -> Unit
        }
    }

    fun deliverPayload(payload: OAuthCallbackPayload) {
        AppLogger.dump(
            "OAuthNavigationBus.deliverPayload",
            "code=${payload.code}\nstate=${payload.state}",
        )
        _callback.tryEmit(payload)
    }
}
