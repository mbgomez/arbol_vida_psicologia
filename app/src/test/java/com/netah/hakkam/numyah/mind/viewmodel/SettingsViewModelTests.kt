package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.app.AppLanguageManager
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLanguageModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetThemeModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetLanguageModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetOnboardingCompletedUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetThemeModeUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTests {

    private lateinit var appLanguageManager: AppLanguageManager
    private lateinit var getLanguageModeUseCase: GetLanguageModeUseCase
    private lateinit var setLanguageModeUseCase: SetLanguageModeUseCase
    private lateinit var getThemeModeUseCase: GetThemeModeUseCase
    private lateinit var setThemeModeUseCase: SetThemeModeUseCase
    private lateinit var getAssessmentHonestyNoticeVisibilityUseCase: GetAssessmentHonestyNoticeVisibilityUseCase
    private lateinit var setAssessmentHonestyNoticeVisibilityUseCase: SetAssessmentHonestyNoticeVisibilityUseCase
    private lateinit var setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        appLanguageManager = mockk(relaxed = true)
        getLanguageModeUseCase = mockk(relaxed = true)
        setLanguageModeUseCase = mockk(relaxed = true)
        getThemeModeUseCase = mockk(relaxed = true)
        setThemeModeUseCase = mockk(relaxed = true)
        getAssessmentHonestyNoticeVisibilityUseCase = mockk(relaxed = true)
        setAssessmentHonestyNoticeVisibilityUseCase = mockk(relaxed = true)
        setOnboardingCompletedUseCase = mockk(relaxed = true)

        every { getLanguageModeUseCase.run() } returns flowOf(AppLanguageMode.SYSTEM)
        every { getThemeModeUseCase.run() } returns flowOf(AppThemeMode.SYSTEM)
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(true)
    }

    @Test
    fun init_exposesThemeAndHonestyPreferenceState() = coroutinesRule.runBlockingTest {
        every { getLanguageModeUseCase.run() } returns flowOf(AppLanguageMode.SPANISH)
        every { getThemeModeUseCase.run() } returns flowOf(AppThemeMode.DARK)
        every { getAssessmentHonestyNoticeVisibilityUseCase.run() } returns flowOf(false)

        val viewModel = SettingsViewModel(
            appLanguageManager = appLanguageManager,
            getLanguageModeUseCase = getLanguageModeUseCase,
            setLanguageModeUseCase = setLanguageModeUseCase,
            getThemeModeUseCase = getThemeModeUseCase,
            setThemeModeUseCase = setThemeModeUseCase,
            getAssessmentHonestyNoticeVisibilityUseCase = getAssessmentHonestyNoticeVisibilityUseCase,
            setAssessmentHonestyNoticeVisibilityUseCase = setAssessmentHonestyNoticeVisibilityUseCase,
            setOnboardingCompletedUseCase = setOnboardingCompletedUseCase
        )

        val uiState = viewModel.uiState.value as SettingsUiState.Ready

        assertEquals(AppLanguageMode.SPANISH, uiState.model.languageMode)
        assertEquals(AppThemeMode.DARK, uiState.model.themeMode)
        assertEquals(false, uiState.model.shouldShowAssessmentHonestyNotice)
    }

    @Test
    fun onLanguageModeSelected_savesPreferenceAndAppliesLocale() = coroutinesRule.runBlockingTest {
        every { setLanguageModeUseCase.run(AppLanguageMode.ENGLISH) } returns flowOf(AppLanguageMode.ENGLISH)

        val viewModel = SettingsViewModel(
            appLanguageManager = appLanguageManager,
            getLanguageModeUseCase = getLanguageModeUseCase,
            setLanguageModeUseCase = setLanguageModeUseCase,
            getThemeModeUseCase = getThemeModeUseCase,
            setThemeModeUseCase = setThemeModeUseCase,
            getAssessmentHonestyNoticeVisibilityUseCase = getAssessmentHonestyNoticeVisibilityUseCase,
            setAssessmentHonestyNoticeVisibilityUseCase = setAssessmentHonestyNoticeVisibilityUseCase,
            setOnboardingCompletedUseCase = setOnboardingCompletedUseCase
        )

        viewModel.onLanguageModeSelected(AppLanguageMode.ENGLISH)

        verify(exactly = 1) { setLanguageModeUseCase.run(AppLanguageMode.ENGLISH) }
        verify(exactly = 1) { appLanguageManager.applyLanguageMode(AppLanguageMode.ENGLISH) }
    }

    @Test
    fun onThemeModeSelected_savesThemePreference() = coroutinesRule.runBlockingTest {
        every { setThemeModeUseCase.run(AppThemeMode.LIGHT) } returns flowOf(AppThemeMode.LIGHT)

        val viewModel = SettingsViewModel(
            appLanguageManager = appLanguageManager,
            getLanguageModeUseCase = getLanguageModeUseCase,
            setLanguageModeUseCase = setLanguageModeUseCase,
            getThemeModeUseCase = getThemeModeUseCase,
            setThemeModeUseCase = setThemeModeUseCase,
            getAssessmentHonestyNoticeVisibilityUseCase = getAssessmentHonestyNoticeVisibilityUseCase,
            setAssessmentHonestyNoticeVisibilityUseCase = setAssessmentHonestyNoticeVisibilityUseCase,
            setOnboardingCompletedUseCase = setOnboardingCompletedUseCase
        )

        viewModel.onThemeModeSelected(AppThemeMode.LIGHT)

        verify(exactly = 1) { setThemeModeUseCase.run(AppThemeMode.LIGHT) }
    }

    @Test
    fun onAssessmentHonestyNoticeChanged_savesPreference() = coroutinesRule.runBlockingTest {
        every { setAssessmentHonestyNoticeVisibilityUseCase.run(false) } returns flowOf(false)

        val viewModel = SettingsViewModel(
            appLanguageManager = appLanguageManager,
            getLanguageModeUseCase = getLanguageModeUseCase,
            setLanguageModeUseCase = setLanguageModeUseCase,
            getThemeModeUseCase = getThemeModeUseCase,
            setThemeModeUseCase = setThemeModeUseCase,
            getAssessmentHonestyNoticeVisibilityUseCase = getAssessmentHonestyNoticeVisibilityUseCase,
            setAssessmentHonestyNoticeVisibilityUseCase = setAssessmentHonestyNoticeVisibilityUseCase,
            setOnboardingCompletedUseCase = setOnboardingCompletedUseCase
        )

        viewModel.onAssessmentHonestyNoticeChanged(false)

        verify(exactly = 1) { setAssessmentHonestyNoticeVisibilityUseCase.run(false) }
    }

    @Test
    fun replayOnboarding_marksOnboardingIncompleteAndInvokesCallback() = coroutinesRule.runBlockingTest {
        every { setOnboardingCompletedUseCase.run(false) } returns flowOf(false)
        var callbackCount = 0

        val viewModel = SettingsViewModel(
            appLanguageManager = appLanguageManager,
            getLanguageModeUseCase = getLanguageModeUseCase,
            setLanguageModeUseCase = setLanguageModeUseCase,
            getThemeModeUseCase = getThemeModeUseCase,
            setThemeModeUseCase = setThemeModeUseCase,
            getAssessmentHonestyNoticeVisibilityUseCase = getAssessmentHonestyNoticeVisibilityUseCase,
            setAssessmentHonestyNoticeVisibilityUseCase = setAssessmentHonestyNoticeVisibilityUseCase,
            setOnboardingCompletedUseCase = setOnboardingCompletedUseCase
        )

        viewModel.replayOnboarding {
            callbackCount += 1
        }

        verify(exactly = 1) { setOnboardingCompletedUseCase.run(false) }
        assertEquals(1, callbackCount)
    }
}
