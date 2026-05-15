package com.habit.gold.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.localization.appStrings
import com.habit.gold.core.navigation.MainTab
import com.habit.gold.core.session.AuthSession
import com.habit.gold.feature.home.presentation.HomeRoute
import org.koin.core.Koin

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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MainShellBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MainBottomNavigationBar(
                selectedTab = selectedTab,
                onSelectTab = onSelectTab,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainShellBackground)
                .padding(innerPadding),
        ) {
            when (selectedTab) {
                MainTab.Home -> HomeRoute(
                    appKoin = appKoin,
                    session = session,
                    onSelectTab = onSelectTab,
                    modifier = Modifier.fillMaxSize(),
                )
                MainTab.Rewards -> MainPlaceholderPage(
                    title = appStrings.shellRewardsTitle,
                    body = appStrings.shellRewardsDescription,
                )
                MainTab.History -> MainPlaceholderPage(
                    title = appStrings.shellHistoryTitle,
                    body = appStrings.shellHistoryDescription,
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

@Composable
private fun MainPlaceholderPage(
    title: String,
    body: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = body,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
