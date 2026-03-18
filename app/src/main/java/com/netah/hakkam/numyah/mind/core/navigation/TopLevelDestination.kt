package com.netah.hakkam.numyah.mind.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

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
