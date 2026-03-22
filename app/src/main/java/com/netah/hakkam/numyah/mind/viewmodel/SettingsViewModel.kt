package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Ready(val model: SettingsUiModel) : SettingsUiState
}

data class SettingsUiModel(
    val languageMode: AppLanguageMode,
    val themeMode: AppThemeMode,
    val shouldShowAssessmentHonestyNotice: Boolean
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appLanguageManager: AppLanguageManager,
    private val getLanguageModeUseCase: GetLanguageModeUseCase,
    private val setLanguageModeUseCase: SetLanguageModeUseCase,
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val getAssessmentHonestyNoticeVisibilityUseCase: GetAssessmentHonestyNoticeVisibilityUseCase,
    private val setAssessmentHonestyNoticeVisibilityUseCase: SetAssessmentHonestyNoticeVisibilityUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getLanguageModeUseCase.run(),
                getThemeModeUseCase.run(),
                getAssessmentHonestyNoticeVisibilityUseCase.run()
            ) { languageMode, themeMode, shouldShowAssessmentHonestyNotice ->
                SettingsUiState.Ready(
                    SettingsUiModel(
                        languageMode = languageMode,
                        themeMode = themeMode,
                        shouldShowAssessmentHonestyNotice = shouldShowAssessmentHonestyNotice
                    )
                )
            }.collect { settingsState ->
                _uiState.value = settingsState
            }
        }
    }

    fun onLanguageModeSelected(languageMode: AppLanguageMode) {
        viewModelScope.launch {
            setLanguageModeUseCase.run(languageMode).collect { }
            appLanguageManager.applyLanguageMode(languageMode)
        }
    }

    fun onThemeModeSelected(themeMode: AppThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase.run(themeMode).collect { }
        }
    }

    fun onAssessmentHonestyNoticeChanged(visible: Boolean) {
        viewModelScope.launch {
            setAssessmentHonestyNoticeVisibilityUseCase.run(visible).collect { }
        }
    }

    fun replayOnboarding(onCompleted: () -> Unit) {
        viewModelScope.launch {
            setOnboardingCompletedUseCase.run(false).collect {
                onCompleted()
            }
        }
    }
}
