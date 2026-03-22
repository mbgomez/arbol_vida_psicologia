package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.domain.usecase.GetThemeModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetOnboardingStatusUseCase
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
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
class AppStateViewModelTests {

    private lateinit var getOnboardingStatusUseCase: GetOnboardingStatusUseCase
    private lateinit var getThemeModeUseCase: GetThemeModeUseCase

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        getOnboardingStatusUseCase = mockk(relaxed = true)
        getThemeModeUseCase = mockk(relaxed = true)
    }

    @Test
    fun init_whenOnboardingCompleted_setsHomeAsStartDestination() = coroutinesRule.runBlockingTest {
        every { getOnboardingStatusUseCase.run() } returns flowOf(true)
        every { getThemeModeUseCase.run() } returns flowOf(AppThemeMode.DARK)

        val viewModel = AppStateViewModel(getOnboardingStatusUseCase, getThemeModeUseCase)

        verify(exactly = 1) { getOnboardingStatusUseCase.run() }
        verify(exactly = 1) { getThemeModeUseCase.run() }
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(AppDestination.Home.route, viewModel.uiState.value.startDestination)
        assertEquals(AppThemeMode.DARK, viewModel.uiState.value.themeMode)
    }

    @Test
    fun init_whenOnboardingNotCompleted_setsOnboardingAsStartDestination() = coroutinesRule.runBlockingTest {
        every { getOnboardingStatusUseCase.run() } returns flowOf(false)
        every { getThemeModeUseCase.run() } returns flowOf(AppThemeMode.SYSTEM)

        val viewModel = AppStateViewModel(getOnboardingStatusUseCase, getThemeModeUseCase)

        verify(exactly = 1) { getOnboardingStatusUseCase.run() }
        verify(exactly = 1) { getThemeModeUseCase.run() }
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(AppDestination.Onboarding.route, viewModel.uiState.value.startDestination)
        assertEquals(AppThemeMode.SYSTEM, viewModel.uiState.value.themeMode)
    }
}
