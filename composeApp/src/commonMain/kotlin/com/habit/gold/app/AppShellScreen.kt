package com.habit.gold.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.localization.appStrings
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.session.AuthSession
import com.habit.gold.core.storage.SecureStorage
import com.habit.gold.core.storage.AppPreferencesStorage
import com.habit.gold.feature.alerts.domain.usecase.GetAlertsUseCase
import com.habit.gold.feature.alerts.domain.usecase.MarkAllAlertsReadUseCase
import com.habit.gold.feature.alerts.presentation.AlertsRouteDependencies
import com.habit.gold.feature.history.presentation.HistoryRoute
import com.habit.gold.feature.history.presentation.HistoryRouteDependencies
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import com.habit.gold.feature.home.domain.usecase.LoadHomeSummaryUseCase
import com.habit.gold.feature.home.presentation.HomeRouteDependencies
import com.habit.gold.feature.home.presentation.HomeRoute
import com.habit.gold.feature.delivery.presentation.DeliveryRouteDependencies
import com.habit.gold.feature.profile.domain.usecase.GetProfileSummaryUseCase
import com.habit.gold.feature.profile.domain.usecase.LogoutProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.RequestDeleteAccountUseCase
import com.habit.gold.feature.profile.domain.usecase.UpdateProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.VerifyProfileKycUseCase
import com.habit.gold.feature.profile.presentation.ProfileBiometricAuthResult
import com.habit.gold.feature.profile.presentation.ProfileBiometricLockScreen
import com.habit.gold.feature.profile.presentation.ProfileRouteDependencies
import com.habit.gold.feature.profile.presentation.ProfileSecurityStore
import com.habit.gold.feature.profile.presentation.rememberProfileBiometricAuthenticator
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsHistoryUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsMilestonesUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsUserFeaturesUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetReferDetailsUseCase
import com.habit.gold.feature.rewards.presentation.RewardsRoute
import com.habit.gold.feature.rewards.presentation.RewardsRouteDependencies
import com.habit.gold.feature.savings.domain.usecase.CancelSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.CreateSavingsMandateSessionUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandatesUseCase
import com.habit.gold.feature.savings.domain.usecase.PauseSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.ResumeSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.UpdateSavingsMandateSessionUseCase
import com.habit.gold.feature.savings.presentation.SavingsRouteDependencies
import com.habit.gold.feature.trade.domain.TradeLivePriceStore
import com.habit.gold.feature.trade.domain.usecase.CreateBuyOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.CreateSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.ExecuteSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeInvoiceUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.PollTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.SetDefaultTradeVpaUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase
import com.habit.gold.feature.trade.domain.usecase.VerifyTradeVpaUseCase
import com.habit.gold.feature.trade.presentation.rememberPlatformTradePaymentLauncher
import com.habit.gold.feature.trade.presentation.TradeRouteDependencies
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.profile_biometric_disabled
import habitgoldmobile.composeapp.generated.resources.profile_biometric_lock_prompt_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_biometric_lock_prompt_title
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.jetbrains.compose.resources.stringResource

private val MainBottomNavShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
private val MainBottomNavBorder = Color(0x26000000)
private val MainBottomNavBackground = Color.White
private val MainBottomNavSelected = HabitGoldPalette.plum
private val MainBottomNavUnselected = Color(0xFF80858F)
private val MainShellBackground = Color(0xFFF8F8FB)

private data class MainTabUi(
    val tab: MainTab,
    val icon: ImageVector,
)

@Composable
fun AppMainShellScreen(
    appKoin: Koin,
    session: AuthSession,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var shouldShowBottomBar by rememberSaveable { mutableStateOf(true) }
    val biometricAuthenticator = rememberProfileBiometricAuthenticator()
    val biometricSecurityStore = remember(appKoin) { ProfileSecurityStore(appKoin.get<SecureStorage>()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val biometricCancelLabel = stringResource(Res.string.common_cancel)
    val biometricPromptTitle = stringResource(Res.string.profile_biometric_lock_prompt_title)
    val biometricPromptSubtitle = stringResource(Res.string.profile_biometric_lock_prompt_subtitle)
    val biometricDisabledMessage = stringResource(
        Res.string.profile_biometric_disabled,
        biometricAuthenticator.label,
    )
    val biometricSnackbarHostState = remember { SnackbarHostState() }
    val biometricScope = rememberCoroutineScope()
    var biometricEnabled by rememberSaveable { mutableStateOf(false) }
    var biometricAuthenticated by rememberSaveable { mutableStateOf(true) }
    var biometricPromptInFlight by remember { mutableStateOf(false) }
    var shouldRearmBiometricOnForeground by rememberSaveable { mutableStateOf(false) }
    var showBiometricUnlockOverlay by remember { mutableStateOf(false) }
    val biometricPromptInFlightState by rememberUpdatedState(biometricPromptInFlight)
    PlatformBackHandler(
        enabled = selectedTab != MainTab.Home,
        onBack = { onSelectTab(MainTab.Home) },
    )

    val paymentLauncher = rememberPlatformTradePaymentLauncher()
    val homeDependencies = remember(appKoin) {
        HomeRouteDependencies(
            loadHomeSummaryUseCase = appKoin.get<LoadHomeSummaryUseCase>(),
            appPreferencesStorage = appKoin.get<AppPreferencesStorage>(),
            getHomePriceHistoryUseCase = appKoin.get<GetHomePriceHistoryUseCase>(),
            deliveryRouteDependencies = appKoin.get<DeliveryRouteDependencies>(),
        )
    }
    val profileDependencies = remember(appKoin) {
        ProfileRouteDependencies(
            getProfileSummaryUseCase = appKoin.get<GetProfileSummaryUseCase>(),
            updateProfileUseCase = appKoin.get<UpdateProfileUseCase>(),
            verifyProfileKycUseCase = appKoin.get<VerifyProfileKycUseCase>(),
            logoutProfileUseCase = appKoin.get<LogoutProfileUseCase>(),
            requestDeleteAccountUseCase = appKoin.get<RequestDeleteAccountUseCase>(),
            getTradeUserVpasUseCase = appKoin.get<GetTradeUserVpasUseCase>(),
            setDefaultTradeVpaUseCase = appKoin.get<SetDefaultTradeVpaUseCase>(),
            verifyTradeVpaUseCase = appKoin.get<VerifyTradeVpaUseCase>(),
            httpClient = appKoin.get<HttpClient>(),
            secureStorage = appKoin.get<SecureStorage>(),
            appVersion = appKoin.get<com.habit.gold.core.config.AppConfig>().appVersion,
        )
    }
    val savingsDependencies = remember(appKoin) {
        SavingsRouteDependencies(
            getSavingsMandatesUseCase = appKoin.get<GetSavingsMandatesUseCase>(),
            getSavingsMandateUseCase = appKoin.get<GetSavingsMandateUseCase>(),
            pauseSavingsMandateUseCase = appKoin.get<PauseSavingsMandateUseCase>(),
            resumeSavingsMandateUseCase = appKoin.get<ResumeSavingsMandateUseCase>(),
            cancelSavingsMandateUseCase = appKoin.get<CancelSavingsMandateUseCase>(),
            createSavingsMandateSessionUseCase = appKoin.get<CreateSavingsMandateSessionUseCase>(),
            updateSavingsMandateSessionUseCase = appKoin.get<UpdateSavingsMandateSessionUseCase>(),
            getTradeAvailableCouponsUseCase = appKoin.get<GetTradeAvailableCouponsUseCase>(),
            validateTradeCouponUseCase = appKoin.get<ValidateTradeCouponUseCase>(),
            paymentLauncher = paymentLauncher,
            livePriceStore = appKoin.get<TradeLivePriceStore>(),
        )
    }
    val tradeDependencies = remember(appKoin, paymentLauncher) {
        TradeRouteDependencies(
            livePriceStore = appKoin.get<TradeLivePriceStore>(),
            createBuyOrderUseCase = appKoin.get<CreateBuyOrderUseCase>(),
            getTradeAvailableCouponsUseCase = appKoin.get<GetTradeAvailableCouponsUseCase>(),
            validateTradeCouponUseCase = appKoin.get<ValidateTradeCouponUseCase>(),
            pollTradeStatusUseCase = appKoin.get<PollTradeStatusUseCase>(),
            paymentLauncher = paymentLauncher,
            createSellOrderUseCase = appKoin.get<CreateSellOrderUseCase>(),
            executeSellOrderUseCase = appKoin.get<ExecuteSellOrderUseCase>(),
            getSellAvailabilityUseCase = appKoin.get<GetSellAvailabilityUseCase>(),
            getTradeUserVpasUseCase = appKoin.get<GetTradeUserVpasUseCase>(),
            getTradeStatusUseCase = appKoin.get<GetTradeStatusUseCase>(),
            getTradeTransactionsUseCase = appKoin.get<GetTradeTransactionsUseCase>(),
            getTradeInvoiceUseCase = appKoin.get<GetTradeInvoiceUseCase>(),
        )
    }
    val historyDependencies = remember(appKoin) {
        HistoryRouteDependencies(
            getTradeTransactionsUseCase = appKoin.get<GetTradeTransactionsUseCase>(),
            getTradeInvoiceUseCase = appKoin.get<GetTradeInvoiceUseCase>(),
        )
    }
    val rewardsDependencies = remember(appKoin) {
        RewardsRouteDependencies(
            getRewardsMilestonesUseCase = appKoin.get<GetRewardsMilestonesUseCase>(),
            getRewardsUserFeaturesUseCase = appKoin.get<GetRewardsUserFeaturesUseCase>(),
            getRewardsHistoryUseCase = appKoin.get<GetRewardsHistoryUseCase>(),
            getReferDetailsUseCase = appKoin.get<GetReferDetailsUseCase>(),
            tradeDependencies = tradeDependencies,
            savingsDependencies = savingsDependencies,
            httpClient = appKoin.get<HttpClient>(),
        )
    }
    val alertsDependencies = remember(appKoin) {
        AlertsRouteDependencies(
            getAlertsUseCase = appKoin.get<GetAlertsUseCase>(),
            markAllAlertsReadUseCase = appKoin.get<MarkAllAlertsReadUseCase>(),
        )
    }

    LaunchedEffect(session.isLoggedIn) {
        tradeDependencies.livePriceStore.setLoggedIn(session.isLoggedIn)
    }

    LaunchedEffect(biometricSecurityStore) {
        biometricEnabled = biometricSecurityStore.read().biometricEnabled
        biometricAuthenticated = !biometricEnabled
        shouldRearmBiometricOnForeground = false
        showBiometricUnlockOverlay = false
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab != MainTab.Home) {
            shouldShowBottomBar = true
        }
    }

    DisposableEffect(lifecycleOwner, biometricEnabled) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (biometricEnabled && !biometricPromptInFlightState) {
                        shouldRearmBiometricOnForeground = true
                    }
                }

                Lifecycle.Event.ON_START -> {
                    if (biometricEnabled && shouldRearmBiometricOnForeground && !biometricPromptInFlightState) {
                        biometricAuthenticated = false
                        shouldRearmBiometricOnForeground = false
                        showBiometricUnlockOverlay = false
                    }
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    suspend fun requestBiometricAuthentication(force: Boolean = false) {
        if (!biometricEnabled || biometricPromptInFlight) return
        if (!force && biometricAuthenticated) return

        showBiometricUnlockOverlay = false
        biometricPromptInFlight = true
        try {
            delay(if (force) 120 else 250)
            val result = runCatching {
                biometricAuthenticator.authenticate(
                    promptTitle = biometricPromptTitle,
                    promptSubtitle = biometricPromptSubtitle,
                    cancelLabel = biometricCancelLabel,
                )
            }.getOrElse { throwable ->
                ProfileBiometricAuthResult.Error(
                    throwable.message ?: "Unable to start biometric authentication right now.",
                )
            }

            when (result) {
                ProfileBiometricAuthResult.Success -> {
                    biometricAuthenticated = true
                    showBiometricUnlockOverlay = false
                }

                is ProfileBiometricAuthResult.Unavailable -> {
                    biometricSecurityStore.setBiometricEnabled(false)
                    biometricEnabled = false
                    biometricAuthenticated = true
                    showBiometricUnlockOverlay = false
                    biometricSnackbarHostState.showSnackbar(result.message.ifBlank { biometricDisabledMessage })
                }

                is ProfileBiometricAuthResult.Error -> {
                    showBiometricUnlockOverlay = true
                }
            }
        } finally {
            biometricPromptInFlight = false
        }
    }

    LaunchedEffect(selectedTab, biometricEnabled, biometricAuthenticated, showBiometricUnlockOverlay) {
        if (biometricEnabled && !biometricAuthenticated && !showBiometricUnlockOverlay) {
            requestBiometricAuthentication()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MainShellBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = biometricSnackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 220),
                    ),
                exit = fadeOut(animationSpec = tween(durationMillis = 120)) +
                    slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 120),
                    ),
            ) {
                MainBottomNavigationBar(
                    selectedTab = selectedTab,
                    onSelectTab = onSelectTab,
                )
            }
        },
    ) { innerPadding ->
        val interactionSource = remember { MutableInteractionSource() }
        val shouldLockShell = biometricEnabled && !biometricAuthenticated
        val shouldShowUnlockCard = shouldLockShell && showBiometricUnlockOverlay && !biometricPromptInFlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainShellBackground)
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (shouldLockShell) {
                            Modifier.blur(18.dp)
                        } else {
                            Modifier
                        },
                    ),
            ) {
                when (selectedTab) {
                    MainTab.Home -> HomeRoute(
                        dependencies = homeDependencies,
                        alertsDependencies = alertsDependencies,
                        profileDependencies = profileDependencies,
                        savingsDependencies = savingsDependencies,
                        tradeDependencies = tradeDependencies,
                        session = session,
                        onSelectTab = onSelectTab,
                        onBottomBarVisibilityChange = { visible ->
                            shouldShowBottomBar = visible
                        },
                        onBiometricStateChanged = { enabled ->
                            biometricScope.launch {
                                biometricEnabled = enabled
                                biometricAuthenticated = true
                                shouldRearmBiometricOnForeground = enabled
                                showBiometricUnlockOverlay = false
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    MainTab.Rewards -> RewardsRoute(
                        dependencies = rewardsDependencies,
                        onBottomBarVisibilityChange = { visible ->
                            shouldShowBottomBar = visible
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    MainTab.History -> HistoryRoute(
                        dependencies = historyDependencies,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            if (shouldLockShell) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x8A09090B))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) {},
                )
            }

            if (shouldShowUnlockCard) {
                ProfileBiometricLockScreen(
                    label = biometricAuthenticator.label,
                    onUnlock = {
                        biometricScope.launch {
                            showBiometricUnlockOverlay = false
                            requestBiometricAuthentication(force = true)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun MainBottomNavigationBar(
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
) {
    val strings = appStrings
    val items = listOf(
        MainTabUi(tab = MainTab.Home, icon = Icons.Default.Home),
        MainTabUi(tab = MainTab.Rewards, icon = Icons.Default.CardGiftcard),
        MainTabUi(tab = MainTab.History, icon = Icons.Default.History),
    )

    NavigationBar(
        modifier = Modifier
            .border(width = 1.dp, color = MainBottomNavBorder, shape = MainBottomNavShape)
            .clip(MainBottomNavShape),
        containerColor = MainBottomNavBackground,
        tonalElevation = 0.dp,
        contentColor = MainBottomNavSelected,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.tab == selectedTab,
                onClick = { onSelectTab(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = strings.mainTabLabel(item.tab),
                    )
                },
                label = {
                    Text(text = strings.mainTabLabel(item.tab))
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainBottomNavSelected,
                    selectedTextColor = MainBottomNavSelected,
                    indicatorColor = MainBottomNavSelected.copy(alpha = 0.10f),
                    unselectedIconColor = MainBottomNavUnselected,
                    unselectedTextColor = MainBottomNavUnselected,
                ),
            )
        }
    }
}
