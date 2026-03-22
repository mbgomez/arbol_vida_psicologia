package com.netah.hakkam.numyah.mind.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import com.netah.hakkam.numyah.mind.ui.nav.route.destinationForRoute
import com.netah.hakkam.numyah.mind.ui.nav.route.topLevelDestinations
import com.netah.hakkam.numyah.mind.ui.screen.AssessmentRoute
import com.netah.hakkam.numyah.mind.ui.screen.HistoryPlaceholderScreen
import com.netah.hakkam.numyah.mind.ui.screen.HomeScreen
import com.netah.hakkam.numyah.mind.ui.screen.LearnCourseRoute
import com.netah.hakkam.numyah.mind.ui.screen.LearnRoute
import com.netah.hakkam.numyah.mind.ui.screen.LearnSectionRoute
import com.netah.hakkam.numyah.mind.ui.screen.OnboardingRoute
import com.netah.hakkam.numyah.mind.ui.screen.SettingsAboutScreen
import com.netah.hakkam.numyah.mind.ui.screen.SettingsPrivacyScreen
import com.netah.hakkam.numyah.mind.ui.screen.ResultsRoute
import com.netah.hakkam.numyah.mind.ui.screen.SettingsScreen
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentUiState
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentViewModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseViewModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionViewModel
import com.netah.hakkam.numyah.mind.viewmodel.SettingsViewModel

@Composable
fun MainNavGraph(
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
            val assessmentViewModel: AssessmentViewModel = hiltViewModel()
            val assessmentUiState by assessmentViewModel.uiState.collectAsState()
            AppShell(
                navController = navController,
                titleOverride = assessmentScreenTitle(assessmentUiState)
            ) { paddingValues ->
                AssessmentRoute(
                    paddingValues = paddingValues,
                    onBackHome = { navController.navigate(AppDestination.Results.route) },
                    viewModel = assessmentViewModel
                )
            }
        }
        composable(AppDestination.Results.route) {
            AppShell(navController = navController) { paddingValues ->
                ResultsRoute(
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
                LearnRoute(
                    paddingValues = paddingValues,
                    onOpenCourse = { courseId ->
                        navController.navigate(AppDestination.LearnCourse.createRoute(courseId))
                    }
                )
            }
        }
        composable(
            route = AppDestination.LearnCourse.route,
            arguments = listOf(
                navArgument(AppDestination.LearnCourse.courseIdArg) { type = NavType.StringType }
            )
        ) {
            val learnCourseViewModel: LearnCourseViewModel = hiltViewModel()
            val learnCourseUiState by learnCourseViewModel.uiState.collectAsState()
            AppShell(
                navController = navController,
                titleOverride = learnCourseScreenTitle(learnCourseUiState),
                showBottomBar = false,
                useDetailHeader = true,
                onBack = { navController.navigateUp() }
            ) { paddingValues ->
                LearnCourseRoute(
                    paddingValues = paddingValues,
                    onOpenSection = { courseId, sectionId ->
                        navController.navigate(
                            AppDestination.LearnSection.createRoute(courseId, sectionId)
                        )
                    },
                    viewModel = learnCourseViewModel
                )
            }
        }
        composable(
            route = AppDestination.LearnSection.route,
            arguments = listOf(
                navArgument(AppDestination.LearnSection.courseIdArg) { type = NavType.StringType },
                navArgument(AppDestination.LearnSection.sectionIdArg) { type = NavType.StringType }
            )
        ) {
            val learnSectionViewModel: LearnSectionViewModel = hiltViewModel()
            val learnSectionUiState by learnSectionViewModel.uiState.collectAsState()
            AppShell(
                navController = navController,
                titleOverride = learnSectionScreenTitle(learnSectionUiState),
                showBottomBar = false,
                useDetailHeader = true,
                onBack = { navController.navigateUp() }
            ) { paddingValues ->
                LearnSectionRoute(
                    paddingValues = paddingValues,
                    onOpenSection = { courseId, sectionId ->
                        navController.navigate(
                            AppDestination.LearnSection.createRoute(courseId, sectionId)
                        )
                    },
                    viewModel = learnSectionViewModel
                )
            }
        }
        composable(AppDestination.Settings.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsUiState by settingsViewModel.uiState.collectAsState()
            AppShell(navController = navController) { paddingValues ->
                SettingsScreen(
                    paddingValues = paddingValues,
                    uiState = settingsUiState,
                    onLanguageModeSelected = settingsViewModel::onLanguageModeSelected,
                    onThemeModeSelected = settingsViewModel::onThemeModeSelected,
                    onAssessmentHonestyNoticeChanged = settingsViewModel::onAssessmentHonestyNoticeChanged,
                    onOpenPrivacy = { navController.navigate(AppDestination.SettingsPrivacy.route) },
                    onOpenAbout = { navController.navigate(AppDestination.SettingsAbout.route) },
                    onReplayOnboarding = {
                        settingsViewModel.replayOnboarding {
                            navController.navigate(AppDestination.Onboarding.route) {
                                popUpTo(AppDestination.Home.route) {
                                    inclusive = false
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    }
                )
            }
        }
        composable(AppDestination.SettingsPrivacy.route) {
            AppShell(
                navController = navController,
                showBottomBar = false,
                useDetailHeader = true,
                onBack = { navController.navigateUp() }
            ) { paddingValues ->
                SettingsPrivacyScreen(paddingValues = paddingValues)
            }
        }
        composable(AppDestination.SettingsAbout.route) {
            AppShell(
                navController = navController,
                showBottomBar = false,
                useDetailHeader = true,
                onBack = { navController.navigateUp() }
            ) { paddingValues ->
                SettingsAboutScreen(paddingValues = paddingValues)
            }
        }
    }
}

@Composable
private fun AppShell(
    navController: NavHostController,
    titleOverride: String? = null,
    showBottomBar: Boolean = true,
    useDetailHeader: Boolean = false,
    onBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val currentTitle = titleOverride ?: destinationTitle(currentRoute)
    var showExitAssessmentDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (useDetailHeader) {
                AppShellDetailHeader(
                    title = currentTitle,
                    onBack = onBack
                )
            } else {
                AppShellHeader(
                    title = currentTitle
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    tonalElevation = NavigationBarDefaults.Elevation
                ) {
                    topLevelDestinations.forEach { topLevelDestination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == topLevelDestination.destination.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute == AppDestination.Assessment.route &&
                                    topLevelDestination.destination.route == AppDestination.Home.route
                                ) {
                                    showExitAssessmentDialog = true
                                } else {
                                    navController.navigate(topLevelDestination.destination.route) {
                                        popUpTo(AppDestination.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            content(paddingValues)
        }
    }

    if (showExitAssessmentDialog) {
        AlertDialog(
            onDismissRequest = { showExitAssessmentDialog = false },
            title = {
                Text(text = stringResource(R.string.assessment_exit_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.assessment_exit_dialog_body))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitAssessmentDialog = false
                        navController.navigate(AppDestination.Home.route) {
                            popUpTo(AppDestination.Home.route) {
                                inclusive = true
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.assessment_exit_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitAssessmentDialog = false }) {
                    Text(text = stringResource(R.string.assessment_exit_dialog_cancel))
                }
            }
        )
    }
}

@Composable
private fun assessmentScreenTitle(uiState: AssessmentUiState): String? {
    val sephiraName = when (uiState) {
        is AssessmentUiState.Intro -> uiState.model.sephiraName
        is AssessmentUiState.Question -> uiState.model.sephiraName
        is AssessmentUiState.Completed -> uiState.model.sephiraName
        else -> null
    }

    return sephiraName?.let {
        stringResource(R.string.screen_assessment_with_sephira, it)
    }
}

@Composable
private fun AppShellHeader(
    title: String
) {
    val outerHorizontalPadding = dimensionResource(R.dimen.spacing_md)
    val outerVerticalPadding = dimensionResource(R.dimen.spacing_sm)
    val containerRadius = dimensionResource(R.dimen.radius_md)
    val contentHorizontalPadding = dimensionResource(R.dimen.spacing_xl)
    val contentTopPadding = dimensionResource(R.dimen.spacing_2xl)
    val contentBottomPadding = dimensionResource(R.dimen.spacing_2xl)
    val columnSpacing = dimensionResource(R.dimen.spacing_md)
    val rowSpacing = dimensionResource(R.dimen.size_dot_sm)
    val indicatorSize = dimensionResource(R.dimen.size_dot_md)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
            .padding(horizontal = outerHorizontalPadding, vertical = outerVerticalPadding),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        contentColor = MaterialTheme.colorScheme.onBackground,
        shadowElevation = dimensionResource(R.dimen.elevation_none),
        shape = RoundedCornerShape(containerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = contentHorizontalPadding,
                    end = contentHorizontalPadding,
                    top = contentTopPadding,
                    bottom = contentBottomPadding
                ),
            verticalArrangement = Arrangement.spacedBy(columnSpacing)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(rowSpacing)
            ) {
                Box(
                    modifier = Modifier
                        .size(indicatorSize)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = CircleShape
                        )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppShellDetailHeader(
    title: String,
    onBack: (() -> Unit)?
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.navigation_back)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun destinationTitle(currentRoute: String?): String {
    return when (currentRoute) {
        AppDestination.Assessment.route -> stringResource(R.string.screen_assessment)

        else -> stringResource(
            destinationForRoute(currentRoute)?.titleRes ?: AppDestination.Home.titleRes
        )
    }
}

@Composable
private fun learnCourseScreenTitle(uiState: LearnCourseUiState): String {
    return when (uiState) {
        is LearnCourseUiState.Loaded -> uiState.model.title
        else -> stringResource(R.string.screen_learn)
    }
}

@Composable
private fun learnSectionScreenTitle(uiState: LearnSectionUiState): String {
    return when (uiState) {
        is LearnSectionUiState.Loaded -> uiState.model.sectionTitle
        else -> stringResource(R.string.screen_learn)
    }
}
