package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.data.repository.OnboardingRepository
import com.netah.hakkam.numyah.mind.ui.nav.route.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppStateUiState(
    val isLoading: Boolean = true,
    val startDestination: String = AppDestination.Onboarding.route
)

@HiltViewModel
class AppStateViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppStateUiState())
    val uiState: StateFlow<AppStateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val hasCompletedOnboarding = onboardingRepository.hasCompletedOnboarding()
            _uiState.value = AppStateUiState(
                isLoading = false,
                startDestination = if (hasCompletedOnboarding) {
                    AppDestination.Home.route
                } else {
                    AppDestination.Onboarding.route
                }
            )
        }
    }
}
