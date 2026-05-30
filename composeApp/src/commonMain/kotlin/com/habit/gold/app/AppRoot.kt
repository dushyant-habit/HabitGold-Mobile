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
import com.habit.gold.core.localization.rememberAppStrings
import com.habit.gold.core.navigation.AppRoute
import com.habit.gold.core.platform.analytics.setPlatformScreenName
import com.habit.gold.core.platform.PlatformBridgeStore
import com.habit.gold.core.session.SessionStore
import com.habit.gold.feature.auth.presentation.AuthFlowScreen
import com.habit.gold.feature.auth.presentation.AuthFlowViewModel
import com.habit.gold.feature.auth.domain.usecase.RequestOtpUseCase
import com.habit.gold.feature.auth.domain.usecase.SubmitBasicDetailsUseCase
import com.habit.gold.feature.auth.domain.usecase.VerifyOtpUseCase
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
            val appStrings: AppStrings = rememberAppStrings()
            val sessionStore: SessionStore = remember(appKoin) { appKoin.get() }

            ProvideAppStrings(appStrings = appStrings) {
                val appRootViewModel = viewModel {
                    AppRootViewModel(
                        sessionStore = sessionStore,
                        startupCoordinator = AppStartupCoordinator(),
                        sessionResetManager = appKoin.get<AuthenticatedSessionResetManager>(),
                    )
                }
                LaunchedEffect(appRootViewModel) {
                    appRootViewModel.start()
                }
                val appState by appRootViewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(appState.currentRoute) {
                    val routeName = when (val route = appState.currentRoute) {
                        AppRoute.Splash -> "Splash"
                        AppRoute.Authentication -> "Authentication"
                        is AppRoute.Main -> "Main_${route.tab.name}"
                    }
                    setPlatformScreenName(routeName)
                }

                when (val currentRoute = appState.currentRoute) {
                    AppRoute.Splash -> AppSplashScreen(modifier = Modifier.fillMaxSize())
                    AppRoute.Authentication -> {
                        val authViewModel = viewModel {
                            AuthFlowViewModel(
                                appConfig = appConfig,
                                platformInfo = appKoin.get(),
                                appStrings = appStrings,
                                requestOtpUseCase = appKoin.get<RequestOtpUseCase>(),
                                verifyOtpUseCase = appKoin.get<VerifyOtpUseCase>(),
                                submitBasicDetailsUseCase = appKoin.get<SubmitBasicDetailsUseCase>(),
                                sessionStore = sessionStore,
                                platformBridgeStore = appKoin.get<PlatformBridgeStore>(),
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
                        appKoin = appKoin,
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
