package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.BuildConfig
import com.netah.hakkam.numyah.mind.app.AppLanguageManager
import com.netah.hakkam.numyah.mind.app.observability.AppTelemetry
import com.netah.hakkam.numyah.mind.app.observability.NonFatalIssueKey
import com.netah.hakkam.numyah.mind.app.observability.SettingsChangeKey
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetAssessmentExitConfirmationVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetLanguageModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetMockHistoryModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetStartupLegalDisclaimerVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetThemeModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentHonestyNoticeVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetAssessmentExitConfirmationVisibilityUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetLanguageModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetMockHistoryModeUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetOnboardingCompletedUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.SetStartupLegalDisclaimerVisibilityUseCase
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
    val shouldShowAssessmentHonestyNotice: Boolean,
    val shouldShowAssessmentExitConfirmation: Boolean,
    val shouldShowStartupLegalDisclaimer: Boolean,
    val showMockHistoryTools: Boolean,
    val isMockHistoryEnabled: Boolean
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
    private val getAssessmentExitConfirmationVisibilityUseCase: GetAssessmentExitConfirmationVisibilityUseCase,
    private val setAssessmentExitConfirmationVisibilityUseCase: SetAssessmentExitConfirmationVisibilityUseCase,
    private val getStartupLegalDisclaimerVisibilityUseCase: GetStartupLegalDisclaimerVisibilityUseCase,
    private val setStartupLegalDisclaimerVisibilityUseCase: SetStartupLegalDisclaimerVisibilityUseCase,
    private val getMockHistoryModeUseCase: GetMockHistoryModeUseCase,
    private val setMockHistoryModeUseCase: SetMockHistoryModeUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val appTelemetry: AppTelemetry
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                combine(
                    getLanguageModeUseCase.run(),
                    getThemeModeUseCase.run(),
                    getAssessmentHonestyNoticeVisibilityUseCase.run()
                ) { languageMode, themeMode, shouldShowAssessmentHonestyNotice ->
                    Triple(languageMode, themeMode, shouldShowAssessmentHonestyNotice)
                },
                combine(
                    getAssessmentExitConfirmationVisibilityUseCase.run(),
                    getStartupLegalDisclaimerVisibilityUseCase.run(),
                    getMockHistoryModeUseCase.run()
                ) { shouldShowAssessmentExitConfirmation, shouldShowStartupLegalDisclaimer, isMockHistoryEnabled ->
                    Triple(
                        shouldShowAssessmentExitConfirmation,
                        shouldShowStartupLegalDisclaimer,
                        isMockHistoryEnabled
                    )
                }
            ) { primaryPreferences, secondaryPreferences ->
                val (languageMode, themeMode, shouldShowAssessmentHonestyNotice) = primaryPreferences
                val (
                    shouldShowAssessmentExitConfirmation,
                    shouldShowStartupLegalDisclaimer,
                    isMockHistoryEnabled
                ) = secondaryPreferences

                SettingsUiState.Ready(
                    SettingsUiModel(
                        languageMode = languageMode,
                        themeMode = themeMode,
                        shouldShowAssessmentHonestyNotice = shouldShowAssessmentHonestyNotice,
                        shouldShowAssessmentExitConfirmation = shouldShowAssessmentExitConfirmation,
                        shouldShowStartupLegalDisclaimer = shouldShowStartupLegalDisclaimer,
                        showMockHistoryTools = BuildConfig.DEBUG,
                        isMockHistoryEnabled = BuildConfig.DEBUG && isMockHistoryEnabled
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
            appTelemetry.trackSettingChanged(
                key = SettingsChangeKey.LANGUAGE,
                value = languageMode.analyticsValue
            )
        }
    }

    fun onThemeModeSelected(themeMode: AppThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase.run(themeMode).collect { }
            appTelemetry.trackSettingChanged(
                key = SettingsChangeKey.THEME,
                value = themeMode.analyticsValue
            )
        }
    }

    fun onAssessmentHonestyNoticeChanged(visible: Boolean) {
        viewModelScope.launch {
            setAssessmentHonestyNoticeVisibilityUseCase.run(visible).collect { }
            appTelemetry.trackSettingChanged(
                key = SettingsChangeKey.HONESTY_NOTICE,
                value = visible.toString()
            )
        }
    }

    fun onAssessmentExitConfirmationChanged(visible: Boolean) {
        viewModelScope.launch {
            setAssessmentExitConfirmationVisibilityUseCase.run(visible).collect { }
            appTelemetry.trackSettingChanged(
                key = SettingsChangeKey.ASSESSMENT_EXIT_CONFIRMATION,
                value = visible.toString()
            )
        }
    }

    fun onStartupLegalDisclaimerChanged(visible: Boolean) {
        viewModelScope.launch {
            setStartupLegalDisclaimerVisibilityUseCase.run(visible).collect { }
            appTelemetry.trackSettingChanged(
                key = SettingsChangeKey.STARTUP_LEGAL_DISCLAIMER,
                value = visible.toString()
            )
        }
    }

    fun onMockHistoryEnabledChanged(enabled: Boolean) {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            setMockHistoryModeUseCase.run(enabled).collect { }
        }
    }

    fun replayOnboarding(onCompleted: () -> Unit) {
        viewModelScope.launch {
            setOnboardingCompletedUseCase.run(false).collect {
                appTelemetry.trackOnboardingReplayed()
                onCompleted()
            }
        }
    }

    fun reportTestNonFatal() {
        if (!BuildConfig.DEBUG) return
        appTelemetry.recordNonFatal(
            key = NonFatalIssueKey.TESTER_VERIFICATION_NON_FATAL,
            throwable = IllegalStateException("Manual tester non-fatal verification"),
            attributes = mapOf("source" to "settings_debug")
        )
    }
}

private val AppLanguageMode.analyticsValue: String
    get() = when (this) {
        AppLanguageMode.SYSTEM -> "system"
        AppLanguageMode.ENGLISH -> "english"
        AppLanguageMode.SPANISH -> "spanish"
    }

private val AppThemeMode.analyticsValue: String
    get() = when (this) {
        AppThemeMode.SYSTEM -> "system"
        AppThemeMode.LIGHT -> "light"
        AppThemeMode.DARK -> "dark"
    }
