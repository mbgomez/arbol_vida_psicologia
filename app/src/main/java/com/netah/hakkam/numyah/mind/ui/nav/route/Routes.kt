package com.netah.hakkam.numyah.mind.ui.nav.route

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.netah.hakkam.numyah.mind.R

sealed class AppDestination(
    val route: String,
    @StringRes val titleRes: Int
) {
    data object Onboarding : AppDestination("onboarding", R.string.screen_onboarding)
    data object Home : AppDestination("home", R.string.nav_home)
    data object Assessment : AppDestination("assessment", R.string.screen_assessment)
    data object Results : AppDestination("results", R.string.screen_results)
    data object History : AppDestination("history", R.string.screen_history)
    data object Learn : AppDestination("learn", R.string.screen_learn)
    data object Settings : AppDestination("settings", R.string.screen_settings)
}

data class TopLevelDestination(
    val destination: AppDestination,
    val icon: ImageVector
)

val topLevelDestinations = listOf(
    TopLevelDestination(AppDestination.Home, Icons.Outlined.Home),
    TopLevelDestination(AppDestination.History, Icons.Outlined.List),
    TopLevelDestination(AppDestination.Learn, Icons.Outlined.Info),
    TopLevelDestination(AppDestination.Settings, Icons.Outlined.Settings)
)

fun destinationForRoute(route: String?): AppDestination? = when (route) {
    AppDestination.Onboarding.route -> AppDestination.Onboarding
    AppDestination.Home.route -> AppDestination.Home
    AppDestination.Assessment.route -> AppDestination.Assessment
    AppDestination.Results.route -> AppDestination.Results
    AppDestination.History.route -> AppDestination.History
    AppDestination.Learn.route -> AppDestination.Learn
    AppDestination.Settings.route -> AppDestination.Settings
    else -> null
}
