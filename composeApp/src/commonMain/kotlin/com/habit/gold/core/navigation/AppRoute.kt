package com.habit.gold.core.navigation

sealed interface AppRoute {
    data object Splash : AppRoute
    data object Authentication : AppRoute
    data class Main(
        val tab: MainTab = MainTab.Home,
    ) : AppRoute
}

enum class MainTab(
    val label: String,
) {
    Home("Home"),
    Transactions("Transactions"),
    Profile("Profile"),
}
