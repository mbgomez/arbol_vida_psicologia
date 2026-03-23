package com.netah.hakkam.numyah.mind.ui.nav

import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import com.netah.hakkam.numyah.mind.ui.nav.route.destinationForRoute
import com.netah.hakkam.numyah.mind.ui.nav.route.topLevelDestinations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class RoutesTests {

    @Test
    fun topLevelDestinations_includeAssessmentsBetweenHomeAndHistory() {
        assertEquals(
            listOf(
                AppDestination.Home,
                AppDestination.AssessmentLibrary,
                AppDestination.History,
                AppDestination.Learn,
                AppDestination.Settings
            ),
            topLevelDestinations.map { it.destination }
        )
    }

    @Test
    fun destinationForRoute_mapsDedicatedHistoryTrendsRoute() {
        assertSame(
            AppDestination.HistoryTrends,
            destinationForRoute(AppDestination.HistoryTrends.route)
        )
    }

    @Test
    fun assessmentRoute_supportsFreshStartQuery() {
        assertEquals(
            "assessment?startFresh=true",
            AppDestination.Assessment.createRoute(startFresh = true)
        )
        assertSame(
            AppDestination.Assessment,
            destinationForRoute(AppDestination.Assessment.routePattern)
        )
    }
}
