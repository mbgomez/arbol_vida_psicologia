package com.netah.hakkam.numyah.mind.data.repository

import android.content.SharedPreferences
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalAppPreferencesRepositoryTests {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var repository: LocalAppPreferencesRepository

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        sharedPreferences = mockk(relaxed = true)
        editor = mockk(relaxed = true)
        every { sharedPreferences.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor

        repository = LocalAppPreferencesRepository(sharedPreferences)
    }

    @Test
    fun setOnboardingCompleted_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setOnboardingCompleted(true).first()

        verify(exactly = 1) { sharedPreferences.edit() }
        verify(exactly = 1) { editor.putBoolean("onboarding_completed", true) }
        verify(exactly = 1) { editor.apply() }
        assertEquals(true, result)
    }

    @Test
    fun hasCompletedOnboarding_readsStoredValue() = coroutinesRule.runBlockingTest {
        every { sharedPreferences.getBoolean("onboarding_completed", false) } returns true

        val result = repository.hasCompletedOnboarding().first()

        verify(exactly = 1) { sharedPreferences.getBoolean("onboarding_completed", false) }
        assertEquals(true, result)
    }

    @Test
    fun setAssessmentHonestyNoticeVisible_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setAssessmentHonestyNoticeVisible(false).first()

        verify(exactly = 1) { sharedPreferences.edit() }
        verify(exactly = 1) { editor.putBoolean("show_assessment_honesty_notice", false) }
        verify(exactly = 1) { editor.apply() }
        assertEquals(false, result)
    }

    @Test
    fun shouldShowAssessmentHonestyNotice_readsStoredValue() = coroutinesRule.runBlockingTest {
        every { sharedPreferences.getBoolean("show_assessment_honesty_notice", true) } returns false

        val result = repository.shouldShowAssessmentHonestyNotice().first()

        verify(exactly = 1) { sharedPreferences.getBoolean("show_assessment_honesty_notice", true) }
        assertEquals(false, result)
    }
}
