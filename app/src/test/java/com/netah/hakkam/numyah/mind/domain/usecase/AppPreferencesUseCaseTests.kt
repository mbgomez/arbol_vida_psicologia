package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppPreferencesUseCaseTests {

    private lateinit var appPreferencesRepository: AppPreferencesRepository
    private lateinit var getOnboardingStatusUseCase: GetOnboardingStatusUseCase
    private lateinit var setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
    private lateinit var getAssessmentHonestyNoticeVisibilityUseCase: GetAssessmentHonestyNoticeVisibilityUseCase
    private lateinit var setAssessmentHonestyNoticeVisibilityUseCase: SetAssessmentHonestyNoticeVisibilityUseCase
    private lateinit var getLanguageModeUseCase: GetLanguageModeUseCase
    private lateinit var setLanguageModeUseCase: SetLanguageModeUseCase
    private lateinit var getThemeModeUseCase: GetThemeModeUseCase
    private lateinit var setThemeModeUseCase: SetThemeModeUseCase
    private lateinit var getCompletedLearningSectionsUseCase: GetCompletedLearningSectionsUseCase
    private lateinit var markLearningSectionCompletedUseCase: MarkLearningSectionCompletedUseCase

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        appPreferencesRepository = mockk(relaxed = true)
        getOnboardingStatusUseCase = GetOnboardingStatusUseCase(appPreferencesRepository)
        setOnboardingCompletedUseCase = SetOnboardingCompletedUseCase(appPreferencesRepository)
        getAssessmentHonestyNoticeVisibilityUseCase =
            GetAssessmentHonestyNoticeVisibilityUseCase(appPreferencesRepository)
        setAssessmentHonestyNoticeVisibilityUseCase =
            SetAssessmentHonestyNoticeVisibilityUseCase(appPreferencesRepository)
        getLanguageModeUseCase = GetLanguageModeUseCase(appPreferencesRepository)
        setLanguageModeUseCase = SetLanguageModeUseCase(appPreferencesRepository)
        getThemeModeUseCase = GetThemeModeUseCase(appPreferencesRepository)
        setThemeModeUseCase = SetThemeModeUseCase(appPreferencesRepository)
        getCompletedLearningSectionsUseCase = GetCompletedLearningSectionsUseCase(appPreferencesRepository)
        markLearningSectionCompletedUseCase = MarkLearningSectionCompletedUseCase(appPreferencesRepository)
    }

    @Test
    fun getOnboardingStatusUseCase_emitsRepositoryValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.hasCompletedOnboarding() } returns flowOf(true)

        val result = getOnboardingStatusUseCase.run().toList()

        verify(exactly = 1) { appPreferencesRepository.hasCompletedOnboarding() }
        assertEquals(listOf(true), result)
    }

    @Test
    fun setOnboardingCompletedUseCase_emitsSavedValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.setOnboardingCompleted(true) } returns flowOf(true)

        val result = setOnboardingCompletedUseCase.run(true).toList()

        verify(exactly = 1) { appPreferencesRepository.setOnboardingCompleted(true) }
        assertEquals(listOf(true), result)
    }

    @Test
    fun getAssessmentHonestyNoticeVisibilityUseCase_emitsRepositoryValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.shouldShowAssessmentHonestyNotice() } returns flowOf(true)

        val result = getAssessmentHonestyNoticeVisibilityUseCase.run().toList()

        verify(exactly = 1) { appPreferencesRepository.shouldShowAssessmentHonestyNotice() }
        assertEquals(listOf(true), result)
    }

    @Test
    fun setAssessmentHonestyNoticeVisibilityUseCase_emitsSavedValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.setAssessmentHonestyNoticeVisible(false) } returns flowOf(false)

        val result = setAssessmentHonestyNoticeVisibilityUseCase.run(false).toList()

        verify(exactly = 1) { appPreferencesRepository.setAssessmentHonestyNoticeVisible(false) }
        assertEquals(listOf(false), result)
    }

    @Test
    fun getLanguageModeUseCase_emitsRepositoryValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.getLanguageMode() } returns flowOf(AppLanguageMode.SPANISH)

        val result = getLanguageModeUseCase.run().toList()

        verify(exactly = 1) { appPreferencesRepository.getLanguageMode() }
        assertEquals(listOf(AppLanguageMode.SPANISH), result)
    }

    @Test
    fun setLanguageModeUseCase_emitsSavedValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.setLanguageMode(AppLanguageMode.ENGLISH) } returns flowOf(AppLanguageMode.ENGLISH)

        val result = setLanguageModeUseCase.run(AppLanguageMode.ENGLISH).toList()

        verify(exactly = 1) { appPreferencesRepository.setLanguageMode(AppLanguageMode.ENGLISH) }
        assertEquals(listOf(AppLanguageMode.ENGLISH), result)
    }

    @Test
    fun getThemeModeUseCase_emitsRepositoryValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.getThemeMode() } returns flowOf(AppThemeMode.DARK)

        val result = getThemeModeUseCase.run().toList()

        verify(exactly = 1) { appPreferencesRepository.getThemeMode() }
        assertEquals(listOf(AppThemeMode.DARK), result)
    }

    @Test
    fun setThemeModeUseCase_emitsSavedValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.setThemeMode(AppThemeMode.LIGHT) } returns flowOf(AppThemeMode.LIGHT)

        val result = setThemeModeUseCase.run(AppThemeMode.LIGHT).toList()

        verify(exactly = 1) { appPreferencesRepository.setThemeMode(AppThemeMode.LIGHT) }
        assertEquals(listOf(AppThemeMode.LIGHT), result)
    }

    @Test
    fun getCompletedLearningSectionsUseCase_emitsRepositoryValue() = coroutinesRule.runBlockingTest {
        every { appPreferencesRepository.getCompletedLearningSections() } returns flowOf(setOf("tree::intro"))

        val result = getCompletedLearningSectionsUseCase.run().toList()

        verify(exactly = 1) { appPreferencesRepository.getCompletedLearningSections() }
        assertEquals(listOf(setOf("tree::intro")), result)
    }

    @Test
    fun markLearningSectionCompletedUseCase_emitsSavedValue() = coroutinesRule.runBlockingTest {
        val params = MarkLearningSectionCompletedParams("tree", "intro")
        every {
            appPreferencesRepository.markLearningSectionCompleted("tree", "intro")
        } returns flowOf(setOf("tree::intro"))

        val result = markLearningSectionCompletedUseCase.run(params).toList()

        verify(exactly = 1) {
            appPreferencesRepository.markLearningSectionCompleted("tree", "intro")
        }
        assertEquals(listOf(setOf("tree::intro")), result)
    }
}
