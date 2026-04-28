package com.habit.gold

import android.app.Application
import com.habit.gold.core.di.startKoinIfNeeded

class HabitGoldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoinIfNeeded()
    }
}
