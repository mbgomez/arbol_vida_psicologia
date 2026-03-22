package com.netah.hakkam.numyah.mind.data.repository

import android.content.SharedPreferences
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
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
        every { editor.putString(any(), any()) } returns editor

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

    @Test
    fun setLanguageMode_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setLanguageMode(AppLanguageMode.SPANISH).first()

        verify(exactly = 1) { sharedPreferences.edit() }
        verify(exactly = 1) { editor.putString("language_mode", "SPANISH") }
        verify(exactly = 1) { editor.apply() }
        assertEquals(AppLanguageMode.SPANISH, result)
    }

    @Test
    fun getLanguageMode_readsStoredValue() = coroutinesRule.runBlockingTest {
        every { sharedPreferences.getString("language_mode", AppLanguageMode.SYSTEM.name) } returns "ENGLISH"

        val result = repository.getLanguageMode().first()

        verify(exactly = 1) { sharedPreferences.getString("language_mode", AppLanguageMode.SYSTEM.name) }
        assertEquals(AppLanguageMode.ENGLISH, result)
    }

    @Test
    fun setThemeMode_savesValueAndEmitsIt() = coroutinesRule.runBlockingTest {
        val result = repository.setThemeMode(AppThemeMode.DARK).first()

        verify(exactly = 1) { sharedPreferences.edit() }
        verify(exactly = 1) { editor.putString("theme_mode", "DARK") }
        verify(exactly = 1) { editor.apply() }
        assertEquals(AppThemeMode.DARK, result)
    }

    @Test
    fun getThemeMode_readsStoredValue() = coroutinesRule.runBlockingTest {
        every { sharedPreferences.getString("theme_mode", AppThemeMode.SYSTEM.name) } returns "LIGHT"

        val result = repository.getThemeMode().first()

        verify(exactly = 1) { sharedPreferences.getString("theme_mode", AppThemeMode.SYSTEM.name) }
        assertEquals(AppThemeMode.LIGHT, result)
    }
}
