package com.habit.gold.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.config.AppConfig
import com.habit.gold.core.di.startKoinIfNeeded
import com.habit.gold.core.localization.AppStrings
import com.habit.gold.core.localization.ProvideAppStrings
import com.habit.gold.core.navigation.AppRoute
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.auth.presentation.AuthFlowScreen
import com.habit.gold.feature.auth.presentation.AuthFlowViewModel
import com.habit.gold.ui.theme.HabitGoldTheme

@Composable
fun AppRoot() {
    HabitGoldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val appKoin = remember { startKoinIfNeeded() }
            val appConfig: AppConfig = remember(appKoin) { appKoin.get() }
            val appStrings: AppStrings = remember(appKoin) { appKoin.get() }
            val sessionStore: SessionStore = remember(appKoin) { appKoin.get() }

            ProvideAppStrings(appStrings = appStrings) {
                val appRootViewModel = viewModel {
                    AppRootViewModel(
                        sessionStore = sessionStore,
                        startupCoordinator = AppStartupCoordinator(),
                    )
                }
                LaunchedEffect(appRootViewModel) {
                    appRootViewModel.start()
                }
                val appState by appRootViewModel.state.collectAsStateWithLifecycle()

                when (val currentRoute = appState.currentRoute) {
                    AppRoute.Splash -> AppSplashScreen(
                        appName = appConfig.appName,
                        modifier = Modifier.fillMaxSize(),
                    )
                    AppRoute.Authentication -> {
                        val authViewModel = viewModel {
                            AuthFlowViewModel(
                                appConfig = appConfig,
                                platformInfo = appKoin.get(),
                                appStrings = appStrings,
                                authRepository = appKoin.get(),
                                sessionStore = sessionStore,
                            )
                        }
                        val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

                        AuthFlowScreen(
                            uiState = uiState,
                            onIntent = authViewModel::onIntent,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    is AppRoute.Main -> AppMainShellScreen(
                        session = appState.session,
                        selectedTab = currentRoute.tab,
                        onSelectTab = { tab ->
                            appRootViewModel.onIntent(AppRootIntent.SelectMainTab(tab))
                        },
                        onLogout = {
                            appRootViewModel.onIntent(AppRootIntent.Logout)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
