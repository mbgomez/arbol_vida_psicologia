package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.observability.OnboardingCompletionMethod
import com.netah.hakkam.numyah.mind.domain.usecase.SetOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
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
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val appTelemetry: AppTelemetry
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onBack() {
        val currentState = _uiState.value
        if (!currentState.isFirstPage) {
            _uiState.value = currentState.copy(currentPage = currentState.currentPage - 1)
        }
    }

    fun onPageChanged(page: Int) {
        val currentState = _uiState.value
        val clampedPage = page.coerceIn(0, currentState.pageCount - 1)
        if (clampedPage != currentState.currentPage) {
            _uiState.value = currentState.copy(currentPage = clampedPage)
        }
    }

    fun onContinue(onFinished: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.isLastPage) {
            completeOnboarding(
                method = OnboardingCompletionMethod.FINISH,
                onFinished = onFinished
            )
        } else {
            _uiState.value = currentState.copy(currentPage = currentState.currentPage + 1)
        }
    }

    fun skip(onFinished: () -> Unit) {
        completeOnboarding(
            method = OnboardingCompletionMethod.SKIP,
            onFinished = onFinished
        )
    }

    private fun completeOnboarding(
        method: OnboardingCompletionMethod,
        onFinished: () -> Unit
    ) {
        viewModelScope.launch {
            setOnboardingCompletedUseCase.run(true).collect {
                appTelemetry.trackOnboardingCompleted(method)
                onFinished()
            }
        }
    }
}
