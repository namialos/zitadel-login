package org.example.zitadellogin.core.session

import java.util.concurrent.atomic.AtomicReference

class AndroidSessionTokenHolder : SessionTokenHolder {
    private val ref = AtomicReference<String?>(null)

    override fun setToken(value: String?) {
        ref.set(value)
    }

    override fun snapshot(): String? = ref.get()
}
