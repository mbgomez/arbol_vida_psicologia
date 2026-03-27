package com.netah.hakkam.numyah.mind.ui.screen

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsDebugTapTests {

    @Test
    fun registerDebugToolsTap_togglesAfterFiveQuickTaps() {
        var progress = DebugTapProgress()
        var toggled = false

        repeat(5) { index ->
            val result = registerDebugToolsTap(
                progress = progress,
                tappedAtMs = 100L + (index * 150L)
            )
            progress = result.first
            toggled = result.second
        }

        assertTrue(toggled)
        assertEquals(DebugTapProgress(), progress)
    }

    @Test
    fun registerDebugToolsTap_resetsSequenceAfterPause() {
        var progress = DebugTapProgress()
        var toggled = false

        repeat(4) { index ->
            val result = registerDebugToolsTap(
                progress = progress,
                tappedAtMs = 100L + (index * 150L)
            )
            progress = result.first
            toggled = result.second
        }

        assertFalse(toggled)
        assertEquals(4, progress.count)

        val pausedTap = registerDebugToolsTap(
            progress = progress,
            tappedAtMs = 2_000L
        )

        assertFalse(pausedTap.second)
        assertEquals(1, pausedTap.first.count)
        assertEquals(2_000L, pausedTap.first.lastTapAtMs)
    }
}
