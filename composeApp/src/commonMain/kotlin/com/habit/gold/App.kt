package com.habit.gold

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.di.startKoinIfNeeded
import com.habit.gold.feature.home.HomeScreen
import com.habit.gold.feature.home.HomeViewModel
import com.habit.gold.ui.theme.HabitGoldTheme

@Composable
@Preview
fun App() {
    HabitGoldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val appKoin = remember { startKoinIfNeeded() }
            val homeViewModel = viewModel {
                HomeViewModel(
                    appConfig = appKoin.get(),
                    platformInfo = appKoin.get(),
                    bootstrapRepository = appKoin.get(),
                )
            }
            val uiState = homeViewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState.value,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
