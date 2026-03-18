package com.netah.hakkam.numyah.mind.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.netah.hakkam.numyah.mind.feature.assessment.AssessmentPlaceholderScreen
import com.netah.hakkam.numyah.mind.feature.history.HistoryPlaceholderScreen
import com.netah.hakkam.numyah.mind.feature.home.HomeScreen
import com.netah.hakkam.numyah.mind.feature.learn.LearnPlaceholderScreen
import com.netah.hakkam.numyah.mind.feature.onboarding.OnboardingRoute
import com.netah.hakkam.numyah.mind.feature.results.ResultsPlaceholderScreen
import com.netah.hakkam.numyah.mind.feature.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppDestination.Onboarding.route) {
            OnboardingRoute(
                onFinish = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppDestination.Home.route) {
            AppShell(navController = navController) { paddingValues ->
                HomeScreen(
                    paddingValues = paddingValues,
                    onStartAssessment = { navController.navigate(AppDestination.Assessment.route) },
                    onOpenResults = { navController.navigate(AppDestination.Results.route) },
                    onOpenHistory = { navController.navigate(AppDestination.History.route) },
                    onOpenLearn = { navController.navigate(AppDestination.Learn.route) },
                    onOpenSettings = { navController.navigate(AppDestination.Settings.route) }
                )
            }
        }
        composable(AppDestination.Assessment.route) {
            AppShell(navController = navController) { paddingValues ->
                AssessmentPlaceholderScreen(
                    paddingValues = paddingValues,
                    onBackHome = { navController.navigate(AppDestination.Home.route) }
                )
            }
        }
        composable(AppDestination.Results.route) {
            AppShell(navController = navController) { paddingValues ->
                ResultsPlaceholderScreen(
                    paddingValues = paddingValues,
                    onBackHome = { navController.navigate(AppDestination.Home.route) }
                )
            }
        }
        composable(AppDestination.History.route) {
            AppShell(navController = navController) { paddingValues ->
                HistoryPlaceholderScreen(paddingValues = paddingValues)
            }
        }
        composable(AppDestination.Learn.route) {
            AppShell(navController = navController) { paddingValues ->
                LearnPlaceholderScreen(paddingValues = paddingValues)
            }
        }
        composable(AppDestination.Settings.route) {
            AppShell(navController = navController) { paddingValues ->
                SettingsScreen(paddingValues = paddingValues)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppShell(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val currentTitle = destinationForRoute(currentRoute)?.titleRes ?: AppDestination.Home.titleRes

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(currentTitle))
                }
            )
        },
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { topLevelDestination ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == topLevelDestination.destination.route
                    } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(topLevelDestination.destination.route) {
                                popUpTo(AppDestination.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = topLevelDestination.icon,
                                contentDescription = stringResource(topLevelDestination.destination.titleRes)
                            )
                        },
                        label = {
                            Text(text = stringResource(topLevelDestination.destination.titleRes))
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            content(paddingValues)
        }
    }
}

private fun destinationForRoute(route: String?): AppDestination? = when (route) {
    AppDestination.Onboarding.route -> AppDestination.Onboarding
    AppDestination.Home.route -> AppDestination.Home
    AppDestination.Assessment.route -> AppDestination.Assessment
    AppDestination.Results.route -> AppDestination.Results
    AppDestination.History.route -> AppDestination.History
    AppDestination.Learn.route -> AppDestination.Learn
    AppDestination.Settings.route -> AppDestination.Settings
    else -> null
}
