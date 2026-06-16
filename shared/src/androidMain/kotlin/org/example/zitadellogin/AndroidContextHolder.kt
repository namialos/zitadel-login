package org.example.zitadellogin

import android.app.Activity
import android.content.Context

/**
 * Holds application context (secure storage) and the foreground [Activity] (Custom Tabs).
 */
object AndroidContextHolder {
    lateinit var applicationContext: Context
        private set

    private var currentActivity: Activity? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun bindActivity(activity: Activity) {
        currentActivity = activity
    }

    fun unbindActivity(activity: Activity) {
        if (currentActivity === activity) {
            currentActivity = null
        }
    }

    fun requireApplicationContext(): Context {
        check(::applicationContext.isInitialized) { "AndroidContextHolder not initialized" }
        return applicationContext
    }

    fun requireActivity(): Activity =
        currentActivity ?: error("Activity is not available. Open the app before signing in.")
}
