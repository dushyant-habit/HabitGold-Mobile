package com.habit.gold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.habit.gold.core.di.startKoinIfNeeded
import com.habit.gold.core.storage.initializePlatformStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_HabitGold)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializePlatformStorage(applicationContext)
        startKoinIfNeeded()

        setContent {
            App()
        }
    }
}
