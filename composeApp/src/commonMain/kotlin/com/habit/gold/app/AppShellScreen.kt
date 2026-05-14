package com.habit.gold.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habit.gold.core.designsystem.AppLoadingView
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.AppSectionCard
import com.habit.gold.core.designsystem.AppSupportingText
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.localization.appStrings
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.session.AuthSession

@Composable
fun AppSplashScreen(
    appName: String,
    modifier: Modifier = Modifier,
) {
    val strings = appStrings
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        AppLoadingView(
            title = appName,
            message = strings.splashPreparingMessage,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun AppMainShellScreen(
    session: AuthSession,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = appStrings
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { onSelectTab(tab) },
                        icon = {
                            Text(
                                text = strings.mainTabLabel(tab).first().toString(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        label = { Text(strings.mainTabLabel(tab)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = HabitGoldDesignSystem.spacing.lg,
                    vertical = HabitGoldDesignSystem.spacing.md,
                ),
            verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.md),
        ) {
            Text(
                text = strings.shellTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            AppSupportingText(text = strings.shellDescription)

            when (selectedTab) {
                MainTab.Home -> MainShellHomeTab(session = session)
                MainTab.Transactions -> MainShellTransactionsTab()
                MainTab.Profile -> MainShellProfileTab(session = session, onLogout = onLogout)
            }
        }
    }
}

@Composable
private fun MainShellHomeTab(session: AuthSession) {
    val strings = appStrings
    AppSectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.sm)) {
            Text(
                text = strings.shellWelcomeBack,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = session.user?.name?.takeIf { it.isNotBlank() } ?: strings.shellTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = strings.shellHomeDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.sm),
    ) {
        MainShellInfoCard(
            title = strings.shellSessionCardTitle,
            value = if (session.isLoggedIn) strings.shellSessionActive else strings.shellSessionLoggedOut,
            modifier = Modifier.weight(1f),
        )
        MainShellInfoCard(
            title = strings.shellProfileCardTitle,
            value = if (session.isProfileComplete) strings.shellProfileComplete else strings.shellProfilePending,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MainShellTransactionsTab() {
    val strings = appStrings
    AppSectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
            Text(
                text = strings.shellTransactionsTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = strings.shellTransactionsDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MainShellProfileTab(
    session: AuthSession,
    onLogout: () -> Unit,
) {
    val strings = appStrings
    AppSectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.sm)) {
            Text(
                text = strings.shellProfileTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            MainShellInfoRow(label = strings.shellPhoneLabel, value = session.user?.phoneNumber.orEmpty())
            MainShellInfoRow(
                label = strings.shellEmailLabel,
                value = session.user?.email?.ifBlank { strings.shellNotAddedYet } ?: strings.shellNotAddedYet,
            )
            MainShellInfoRow(
                label = strings.shellPinCodeLabel,
                value = session.user?.pinCode?.ifBlank { strings.shellNotAddedYet } ?: strings.shellNotAddedYet,
            )
            Spacer(modifier = Modifier.size(4.dp))
            AppPrimaryButton(
                label = strings.shellLogoutCta,
                onClick = onLogout,
            )
        }
    }
}

@Composable
private fun MainShellInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = HabitGoldDesignSystem.radii.md,
    ) {
        Column(
            modifier = Modifier.padding(HabitGoldDesignSystem.spacing.md),
            verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MainShellInfoRow(
    label: String,
    value: String,
) {
    val strings = appStrings
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (value == strings.shellNotAddedYet) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                Color.Unspecified
            },
            textAlign = TextAlign.End,
        )
    }
}
