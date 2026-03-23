package com.netah.hakkam.numyah.mind.ui.nav.route

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import androidx.compose.ui.graphics.vector.ImageVector
import com.netah.hakkam.numyah.mind.R

sealed class AppDestination(
    val route: String,
    @StringRes val titleRes: Int
) {
    data object Onboarding : AppDestination("onboarding", R.string.screen_onboarding)
    data object Home : AppDestination("home", R.string.nav_home)
    data object AssessmentLibrary : AppDestination("assessment-library", R.string.screen_assessment_library)
    data object Assessment : AppDestination("assessment", R.string.screen_assessment) {
        const val startFreshArg = "startFresh"
        const val routePattern = "assessment?$startFreshArg={$startFreshArg}"

        fun createRoute(startFresh: Boolean = false): String {
            return if (startFresh) {
                "$route?$startFreshArg=true"
            } else {
                route
            }
        }
    }
    data object Results : AppDestination("results", R.string.screen_results) {
        const val sessionIdArg = "sessionId"
        const val routePattern = "results?sessionId={sessionId}"

        fun createRoute(sessionId: Long? = null): String {
            return if (sessionId == null) {
                route
            } else {
                "$route?$sessionIdArg=$sessionId"
            }
        }
    }
    data object History : AppDestination("history", R.string.screen_history)
    data object HistoryTrends : AppDestination("history/trends", R.string.screen_history_trends)
    data object ResultsDetail :
        AppDestination("results/detail/{sephiraId}?sessionId={sessionId}", R.string.screen_results_detail) {
        const val sephiraIdArg = "sephiraId"
        const val sessionIdArg = "sessionId"
        const val routePattern = "results/detail/{$sephiraIdArg}?$sessionIdArg={$sessionIdArg}"

        fun createRoute(sephiraId: SephiraId, sessionId: Long? = null): String {
            return if (sessionId == null) {
                "results/detail/${sephiraId.name}"
            } else {
                "results/detail/${sephiraId.name}?$sessionIdArg=$sessionId"
            }
        }
    }
    data object Learn : AppDestination("learn", R.string.screen_learn)
    data object LearnCourse : AppDestination("learn/course/{courseId}", R.string.screen_learn) {
        const val courseIdArg = "courseId"

        fun createRoute(courseId: String): String = "learn/course/$courseId"
    }
    data object LearnSection :
        AppDestination("learn/course/{courseId}/section/{sectionId}", R.string.screen_learn) {
        const val courseIdArg = "courseId"
        const val sectionIdArg = "sectionId"

        fun createRoute(courseId: String, sectionId: String): String {
            return "learn/course/$courseId/section/$sectionId"
        }
    }
    data object Settings : AppDestination("settings", R.string.screen_settings)
    data object SettingsPrivacy : AppDestination("settings/privacy", R.string.screen_settings_privacy)
    data object SettingsAbout : AppDestination("settings/about", R.string.screen_settings_about)
}

data class TopLevelDestination(
    val destination: AppDestination,
    val icon: ImageVector,
    @StringRes val navLabelRes: Int = destination.titleRes
)

val topLevelDestinations = listOf(
    TopLevelDestination(AppDestination.Home, Icons.Outlined.Home),
    TopLevelDestination(
        destination = AppDestination.AssessmentLibrary,
        icon = Icons.Outlined.Assignment,
        navLabelRes = R.string.nav_assessments
    ),
    TopLevelDestination(AppDestination.History, Icons.Outlined.History),
    TopLevelDestination(AppDestination.Learn, Icons.Outlined.Info),
    TopLevelDestination(AppDestination.Settings, Icons.Outlined.Settings)
)

fun destinationForRoute(route: String?): AppDestination? = when (route) {
    AppDestination.Onboarding.route -> AppDestination.Onboarding
    AppDestination.Home.route -> AppDestination.Home
    AppDestination.AssessmentLibrary.route -> AppDestination.AssessmentLibrary
    AppDestination.Assessment.route,
    AppDestination.Assessment.routePattern -> AppDestination.Assessment
    AppDestination.Results.route,
    AppDestination.Results.routePattern -> AppDestination.Results
    AppDestination.History.route -> AppDestination.History
    AppDestination.HistoryTrends.route -> AppDestination.HistoryTrends
    AppDestination.ResultsDetail.route,
    AppDestination.ResultsDetail.routePattern -> AppDestination.ResultsDetail
    AppDestination.Learn.route -> AppDestination.Learn
    AppDestination.LearnCourse.route -> AppDestination.LearnCourse
    AppDestination.LearnSection.route -> AppDestination.LearnSection
    AppDestination.Settings.route -> AppDestination.Settings
    AppDestination.SettingsPrivacy.route -> AppDestination.SettingsPrivacy
    AppDestination.SettingsAbout.route -> AppDestination.SettingsAbout
    else -> null
}
