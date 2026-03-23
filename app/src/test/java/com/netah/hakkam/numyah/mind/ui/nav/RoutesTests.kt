package com.netah.hakkam.numyah.mind.ui.nav

import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import com.netah.hakkam.numyah.mind.ui.nav.route.topLevelDestinations
import org.junit.Assert.assertEquals
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
}
