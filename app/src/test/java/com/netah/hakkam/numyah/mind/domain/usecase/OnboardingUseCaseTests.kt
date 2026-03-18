package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.OnboardingRepository
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
class OnboardingUseCaseTests {

    private lateinit var onboardingRepository: OnboardingRepository
    private lateinit var getOnboardingStatusUseCase: GetOnboardingStatusUseCase
    private lateinit var setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        onboardingRepository = mockk(relaxed = true)
        getOnboardingStatusUseCase = GetOnboardingStatusUseCase(onboardingRepository)
        setOnboardingCompletedUseCase = SetOnboardingCompletedUseCase(onboardingRepository)
    }

    @Test
    fun getOnboardingStatusUseCase_emitsRepositoryValue() = coroutinesRule.runBlockingTest {
        every { onboardingRepository.hasCompletedOnboarding() } returns flowOf(true)

        val result = getOnboardingStatusUseCase.run().toList()

        verify(exactly = 1) { onboardingRepository.hasCompletedOnboarding() }
        assertEquals(listOf(true), result)
    }

    @Test
    fun setOnboardingCompletedUseCase_emitsSavedValue() = coroutinesRule.runBlockingTest {
        every { onboardingRepository.setOnboardingCompleted(true) } returns flowOf(true)

        val result = setOnboardingCompletedUseCase.run(true).toList()

        verify(exactly = 1) { onboardingRepository.setOnboardingCompleted(true) }
        assertEquals(listOf(true), result)
    }
}
