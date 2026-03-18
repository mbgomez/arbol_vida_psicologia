package com.netah.hakkam.numyah.mind.core.navigation

import androidx.annotation.StringRes
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
