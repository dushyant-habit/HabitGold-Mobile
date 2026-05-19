package com.habit.gold

import android.app.Application
import android.os.Build
import com.habit.gold.core.storage.initializePlatformStorage
import com.habit.gold.core.di.startKoinIfNeeded
import com.google.firebase.FirebaseApp
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

class HabitGoldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializePlatformStorage(this)
        FirebaseApp.initializeApp(this)
        startKoinIfNeeded()
        initializeClarity()
    }

    private fun initializeClarity() {
        if (!BuildConfig.CLARITY_ENABLED) return
        val projectId = BuildConfig.CLARITY_PROJECT_ID.trim()
        if (projectId.isEmpty()) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        Clarity.initialize(
            this,
            ClarityConfig(
                projectId = projectId,
                logLevel = if (BuildConfig.DEBUG) LogLevel.Verbose else LogLevel.None,
            ),
        )
    }
}
