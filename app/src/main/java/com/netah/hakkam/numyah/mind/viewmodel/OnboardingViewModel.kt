package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.data.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentPage: Int = 0,
    val pageCount: Int = 5
) {
    val isFirstPage: Boolean = currentPage == 0
    val isLastPage: Boolean = currentPage == pageCount - 1
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onBack() {
        val currentState = _uiState.value
        if (!currentState.isFirstPage) {
            _uiState.value = currentState.copy(currentPage = currentState.currentPage - 1)
        }
    }

    fun onContinue(onFinished: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.isLastPage) {
            completeOnboarding(onFinished)
        } else {
            _uiState.value = currentState.copy(currentPage = currentState.currentPage + 1)
        }
    }

    fun skip(onFinished: () -> Unit) {
        completeOnboarding(onFinished)
    }

    private fun completeOnboarding(onFinished: () -> Unit) {
        viewModelScope.launch {
            onboardingRepository.setOnboardingCompleted(true)
            onFinished()
        }
    }
}
