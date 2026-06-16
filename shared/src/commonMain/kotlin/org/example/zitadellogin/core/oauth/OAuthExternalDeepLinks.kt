package org.example.zitadellogin.core.oauth

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.example.zitadellogin.core.logging.AppLogger

/**
 * Delivers OAuth redirect URLs from the system browser / deep link entry points.
 */
interface OAuthExternalDeepLinks {
    val events: SharedFlow<String>
    fun offer(url: String)
}

class OAuthExternalDeepLinksImpl : OAuthExternalDeepLinks {
    private val _events = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val events: SharedFlow<String> = _events.asSharedFlow()

    override fun offer(url: String) {
        AppLogger.i("OAuthExternalDeepLinks.offer")
        AppLogger.dump("OAuthExternalDeepLinks url", url)
        _events.tryEmit(url)
    }
}
