package org.example.zitadellogin.core.session

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLock

@OptIn(ExperimentalForeignApi::class)
class IosSessionTokenHolder : SessionTokenHolder {
    private val lock = NSLock()
    private var token: String? = null

    override fun setToken(value: String?) {
        lock.lock()
        try {
            token = value
        } finally {
            lock.unlock()
        }
    }

    override fun snapshot(): String? {
        lock.lock()
        return try {
            token
        } finally {
            lock.unlock()
        }
    }
}
