package com.smartsafe.smartsafe_app

import android.app.Activity
import android.app.Application
import com.smartsafe.smartsafe_app.util.SmartSaveActivityLifecycleCallbacks
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartSafeApplication : Application() {

    private val activityLifecycleCallbacks = SmartSaveActivityLifecycleCallbacks()

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun currentActivity(): Activity? = activityLifecycleCallbacks.currentActivity
}