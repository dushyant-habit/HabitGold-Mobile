package com.habit.gold

import android.app.Application
import android.os.Build
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import com.habit.gold.core.storage.initializePlatformStorage
import com.habit.gold.core.di.startKoinIfNeeded
import com.google.firebase.FirebaseApp
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

@OptIn(ExperimentalComposeUiApi::class)
class HabitGoldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Disable Compose adaptive refresh-rate hints on API 35+ to avoid
        // AndroidComposeView repeatedly logging setRequestedFrameRate(NaN).
        ComposeUiFlags.isAdaptiveRefreshRateEnabled = false
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
                logLevel = LogLevel.None,
            ),
        )
    }
}
