package com.netah.hakkam.numyah.mind.ui.nav

import org.junit.Assert.assertEquals
import org.junit.Test

class MainNavTests {

    @Test
    fun assessmentExitNavigationConfig_doesNotSaveOrRestoreState() {
        val config = assessmentExitNavigationConfig()

        assertEquals(false, config.saveState)
        assertEquals(false, config.restoreState)
    }

    @Test
    fun defaultTopLevelNavigationConfig_keepsSaveAndRestoreEnabled() {
        val config = TopLevelNavigationConfig()

        assertEquals(true, config.saveState)
        assertEquals(true, config.restoreState)
    }
}
