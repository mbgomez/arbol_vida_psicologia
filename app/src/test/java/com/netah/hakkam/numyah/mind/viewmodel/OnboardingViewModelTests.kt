package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.usecase.SetOnboardingCompletedUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTests {

    private lateinit var setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
    private lateinit var onboardingViewModel: OnboardingViewModel

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        setOnboardingCompletedUseCase = mockk(relaxed = true)
        onboardingViewModel = OnboardingViewModel(setOnboardingCompletedUseCase)
    }

    @Test
    fun onBack_onFirstPage_keepsCurrentPageAtZero() {
        onboardingViewModel.onBack()

        assertEquals(0, onboardingViewModel.uiState.value.currentPage)
    }

    @Test
    fun onContinue_beforeLastPage_advancesPage() {
        onboardingViewModel.onContinue {}

        assertEquals(1, onboardingViewModel.uiState.value.currentPage)
    }

    @Test
    fun onContinue_onLastPage_completesOnboardingAndInvokesCallback() = coroutinesRule.runBlockingTest {
        every { setOnboardingCompletedUseCase.run(true) } returns flowOf(true)
        var callbackInvoked = false

        repeat(4) {
            onboardingViewModel.onContinue {}
        }
        onboardingViewModel.onContinue {
            callbackInvoked = true
        }

        verify(exactly = 1) { setOnboardingCompletedUseCase.run(true) }
        assertTrue(callbackInvoked)
    }

    @Test
    fun skip_completesOnboardingAndInvokesCallback() = coroutinesRule.runBlockingTest {
        every { setOnboardingCompletedUseCase.run(true) } returns flowOf(true)
        var callbackInvoked = false

        onboardingViewModel.skip {
            callbackInvoked = true
        }

        verify(exactly = 1) { setOnboardingCompletedUseCase.run(true) }
        assertTrue(callbackInvoked)
    }
}
