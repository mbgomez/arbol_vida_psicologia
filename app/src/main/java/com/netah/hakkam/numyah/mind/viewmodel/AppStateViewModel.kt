package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentExitConfirmationVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetThemeModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetOnboardingStatusUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetStartupLegalDisclaimerVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentExitConfirmationVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetStartupLegalDisclaimerVisibilityUseCase
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AppStateUiState(
    val isLoading: Boolean = true,
    val startDestination: String = AppDestination.Onboarding.route,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val shouldShowStartupLegalDisclaimer: Boolean = true,
    val shouldShowAssessmentExitConfirmation: Boolean = true
)

@HiltViewModel
class AppStateViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val getStartupLegalDisclaimerVisibilityUseCase: GetStartupLegalDisclaimerVisibilityUseCase,
    private val setStartupLegalDisclaimerVisibilityUseCase: SetStartupLegalDisclaimerVisibilityUseCase,
    private val getAssessmentExitConfirmationVisibilityUseCase: GetAssessmentExitConfirmationVisibilityUseCase,
    private val setAssessmentExitConfirmationVisibilityUseCase: SetAssessmentExitConfirmationVisibilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppStateUiState())
    val uiState: StateFlow<AppStateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getOnboardingStatusUseCase.run(),
                getThemeModeUseCase.run(),
                getStartupLegalDisclaimerVisibilityUseCase.run(),
                getAssessmentExitConfirmationVisibilityUseCase.run()
            ) { hasCompletedOnboarding, themeMode, shouldShowStartupLegalDisclaimer, shouldShowAssessmentExitConfirmation ->
                AppStateUiState(
                    isLoading = false,
                    startDestination = if (hasCompletedOnboarding) {
                        if (shouldShowStartupLegalDisclaimer) {
                            AppDestination.LegalDisclaimer.route
                        } else {
                            AppDestination.Home.route
                        }
                    } else {
                        AppDestination.Onboarding.route
                    },
                    themeMode = themeMode,
                    shouldShowStartupLegalDisclaimer = shouldShowStartupLegalDisclaimer,
                    shouldShowAssessmentExitConfirmation = shouldShowAssessmentExitConfirmation
                )
            }.collect { appState ->
                _uiState.value = appState
            }
        }
    }

    fun setStartupLegalDisclaimerVisible(visible: Boolean) {
        viewModelScope.launch {
            setStartupLegalDisclaimerVisibilityUseCase.run(visible).collect()
        }
    }

    fun setAssessmentExitConfirmationVisible(visible: Boolean) {
        viewModelScope.launch {
            setAssessmentExitConfirmationVisibilityUseCase.run(visible).collect()
        }
    }
}
