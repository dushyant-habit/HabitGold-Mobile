package com.habit.gold.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
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
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.session.AuthSession

@Composable
fun AppSplashScreen(
    appName: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        AppLoadingView(
            title = appName,
            message = "Preparing your shared HabitGold experience...",
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
                                text = tab.label.first().toString(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        label = { Text(tab.label) },
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
                text = "HabitGold",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            AppSupportingText(
                text = "Phase 4 app shell is live. This shared container will host the real Home, Transactions, and Profile features as we migrate them.",
            )

            when (selectedTab) {
                MainTab.Home -> MainShellHomeTab(session)
                MainTab.Transactions -> MainShellTransactionsTab()
                MainTab.Profile -> MainShellProfileTab(session = session, onLogout = onLogout)
            }
        }
    }
}

@Composable
private fun MainShellHomeTab(session: AuthSession) {
    AppSectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.sm)) {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = session.user?.name?.takeIf { it.isNotBlank() } ?: "HabitGold user",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Your shared post-login shell is ready. Home widgets and portfolio cards will plug into this screen during the next feature migration.",
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
            title = "Session",
            value = if (session.isLoggedIn) "Active" else "Logged out",
            modifier = Modifier.weight(1f),
        )
        MainShellInfoCard(
            title = "Profile",
            value = if (session.isProfileComplete) "Complete" else "Pending",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MainShellTransactionsTab() {
    AppSectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
            Text(
                text = "Transactions shell",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "This placeholder keeps the bottom-navigation contract real while we migrate transaction history, status, invoices, and related drilldowns.",
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
    AppSectionCard {
        Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.sm)) {
            Text(
                text = "Profile shell",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            MainShellInfoRow(label = "Phone", value = session.user?.phoneNumber.orEmpty())
            MainShellInfoRow(
                label = "Email",
                value = session.user?.email?.ifBlank { "Not added yet" } ?: "Not added yet",
            )
            MainShellInfoRow(
                label = "Pincode",
                value = session.user?.pinCode?.ifBlank { "Not added yet" } ?: "Not added yet",
            )
            Spacer(modifier = Modifier.size(4.dp))
            AppPrimaryButton(
                label = "Log out",
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
            color = if (value == "Not added yet") {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                Color.Unspecified
            },
            textAlign = TextAlign.End,
        )
    }
}
