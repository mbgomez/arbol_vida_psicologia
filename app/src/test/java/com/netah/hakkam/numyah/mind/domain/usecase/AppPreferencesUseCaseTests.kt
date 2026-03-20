package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AppPreferencesRepository
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
}
