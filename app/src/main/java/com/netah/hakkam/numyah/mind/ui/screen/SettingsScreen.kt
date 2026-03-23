package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.ui.components.AppActionCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSectionCard
import com.netah.hakkam.numyah.mind.ui.components.PreferenceActionRow
import com.netah.hakkam.numyah.mind.ui.components.PreferenceSelectionRow
import com.netah.hakkam.numyah.mind.ui.components.PreferenceToggleRow
import com.netah.hakkam.numyah.mind.viewmodel.SettingsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.SettingsUiState

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    uiState: SettingsUiState,
    onLanguageModeSelected: (AppLanguageMode) -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onAssessmentHonestyNoticeChanged: (Boolean) -> Unit,
    onMockHistoryEnabledChanged: (Boolean) -> Unit,
    onReportTestNonFatal: () -> Unit,
    onForceTestCrash: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onReplayOnboarding: () -> Unit
) {
    when (uiState) {
        SettingsUiState.Loading -> SettingsLoadingScreen(paddingValues = paddingValues)
        is SettingsUiState.Ready -> SettingsContent(
            paddingValues = paddingValues,
            model = uiState.model,
            onLanguageModeSelected = onLanguageModeSelected,
            onThemeModeSelected = onThemeModeSelected,
            onAssessmentHonestyNoticeChanged = onAssessmentHonestyNoticeChanged,
            onMockHistoryEnabledChanged = onMockHistoryEnabledChanged,
            onReportTestNonFatal = onReportTestNonFatal,
            onForceTestCrash = onForceTestCrash,
            onOpenPrivacy = onOpenPrivacy,
            onOpenAbout = onOpenAbout,
            onReplayOnboarding = onReplayOnboarding
        )
    }
}

@Composable
private fun SettingsLoadingScreen(
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag("settings_loading_indicator"),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsContent(
    paddingValues: PaddingValues,
    model: SettingsUiModel,
    onLanguageModeSelected: (AppLanguageMode) -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onAssessmentHonestyNoticeChanged: (Boolean) -> Unit,
    onMockHistoryEnabledChanged: (Boolean) -> Unit,
    onReportTestNonFatal: () -> Unit,
    onForceTestCrash: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onReplayOnboarding: () -> Unit
) {
    var showReplayOnboardingDialog by remember { mutableStateOf(false) }
    var showMockHistoryDialog by remember { mutableStateOf(false) }

    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag("settings_scroll")
    ) {
        SettingsHero()
        AppearanceSection(
            selectedThemeMode = model.themeMode,
            onThemeModeSelected = onThemeModeSelected
        )
        LanguageSection(
            selectedLanguageMode = model.languageMode,
            onLanguageModeSelected = onLanguageModeSelected
        )
        AssessmentSection(
            shouldShowAssessmentHonestyNotice = model.shouldShowAssessmentHonestyNotice,
            onAssessmentHonestyNoticeChanged = onAssessmentHonestyNoticeChanged
        )
        if (model.showMockHistoryTools) {
            MockHistorySection(
                isMockHistoryEnabled = model.isMockHistoryEnabled,
                onMockHistoryEnabledChanged = { enabled ->
                    if (enabled) {
                        showMockHistoryDialog = true
                    } else {
                        onMockHistoryEnabledChanged(false)
                    }
                }
            )
            ObservabilityChecksSection(
                onReportTestNonFatal = onReportTestNonFatal,
                onForceTestCrash = onForceTestCrash
            )
        }
        OnboardingSection(
            onReplayOnboarding = { showReplayOnboardingDialog = true }
        )
        SettingsNavigationCard(
            title = stringResource(R.string.settings_privacy_title),
            body = stringResource(R.string.settings_privacy_body),
            actionLabel = stringResource(R.string.settings_open_detail_action),
            testTag = "settings_privacy_card",
            onClick = onOpenPrivacy
        )
        SettingsNavigationCard(
            title = stringResource(R.string.settings_about_title),
            body = stringResource(R.string.settings_about_body),
            actionLabel = stringResource(R.string.settings_open_detail_action),
            testTag = "settings_about_card",
            onClick = onOpenAbout
        )
    }

    if (showMockHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showMockHistoryDialog = false },
            title = { Text(text = stringResource(R.string.settings_mock_history_dialog_title)) },
            text = { Text(text = stringResource(R.string.settings_mock_history_dialog_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMockHistoryDialog = false
                        onMockHistoryEnabledChanged(true)
                    }
                ) {
                    Text(text = stringResource(R.string.settings_mock_history_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showMockHistoryDialog = false }) {
                    Text(text = stringResource(R.string.settings_mock_history_dialog_cancel))
                }
            }
        )
    }

    if (showReplayOnboardingDialog) {
        AlertDialog(
            onDismissRequest = { showReplayOnboardingDialog = false },
            title = { Text(text = stringResource(R.string.settings_replay_onboarding_dialog_title)) },
            text = { Text(text = stringResource(R.string.settings_replay_onboarding_dialog_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReplayOnboardingDialog = false
                        onReplayOnboarding()
                    }
                ) {
                    Text(text = stringResource(R.string.settings_replay_onboarding_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showReplayOnboardingDialog = false }) {
                    Text(text = stringResource(R.string.settings_replay_onboarding_cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsNavigationCard(
    title: String,
    body: String,
    actionLabel: String,
    testTag: String,
    onClick: () -> Unit
) {
    AppActionCard(
        title = title,
        body = body,
        actionLabel = actionLabel,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        onClick = onClick
    )
}

@Composable
private fun SettingsHero() {
    AppHeroCard(
        eyebrow = stringResource(R.string.settings_eyebrow),
        title = stringResource(R.string.settings_title),
        body = stringResource(R.string.settings_body)
    )
}

@Composable
private fun AppearanceSection(
    selectedThemeMode: AppThemeMode,
    onThemeModeSelected: (AppThemeMode) -> Unit
) {
    SettingsSectionCard(
        title = stringResource(R.string.settings_appearance_title),
        body = stringResource(R.string.settings_appearance_body)
    ) {
        ThemeModeOption(
            title = stringResource(R.string.settings_theme_system_title),
            body = stringResource(R.string.settings_theme_system_body),
            selected = selectedThemeMode == AppThemeMode.SYSTEM,
            testTag = "settings_theme_system",
            onClick = { onThemeModeSelected(AppThemeMode.SYSTEM) }
        )
        ThemeModeOption(
            title = stringResource(R.string.settings_theme_light_title),
            body = stringResource(R.string.settings_theme_light_body),
            selected = selectedThemeMode == AppThemeMode.LIGHT,
            testTag = "settings_theme_light",
            onClick = { onThemeModeSelected(AppThemeMode.LIGHT) }
        )
        ThemeModeOption(
            title = stringResource(R.string.settings_theme_dark_title),
            body = stringResource(R.string.settings_theme_dark_body),
            selected = selectedThemeMode == AppThemeMode.DARK,
            testTag = "settings_theme_dark",
            onClick = { onThemeModeSelected(AppThemeMode.DARK) }
        )
    }
}

@Composable
private fun LanguageSection(
    selectedLanguageMode: AppLanguageMode,
    onLanguageModeSelected: (AppLanguageMode) -> Unit
) {
    SettingsSectionCard(
        title = stringResource(R.string.settings_language_title),
        body = stringResource(R.string.settings_language_body),
    ) {
        ThemeModeOption(
            title = stringResource(R.string.settings_language_system_title),
            body = stringResource(R.string.settings_language_system_body),
            selected = selectedLanguageMode == AppLanguageMode.SYSTEM,
            testTag = "settings_language_system",
            onClick = { onLanguageModeSelected(AppLanguageMode.SYSTEM) }
        )
        ThemeModeOption(
            title = stringResource(R.string.settings_language_english_title),
            body = stringResource(R.string.settings_language_english_body),
            selected = selectedLanguageMode == AppLanguageMode.ENGLISH,
            testTag = "settings_language_english",
            onClick = { onLanguageModeSelected(AppLanguageMode.ENGLISH) }
        )
        ThemeModeOption(
            title = stringResource(R.string.settings_language_spanish_title),
            body = stringResource(R.string.settings_language_spanish_body),
            selected = selectedLanguageMode == AppLanguageMode.SPANISH,
            testTag = "settings_language_spanish",
            onClick = { onLanguageModeSelected(AppLanguageMode.SPANISH) }
        )
        Text(
            text = stringResource(R.string.settings_language_supporting),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun AssessmentSection(
    shouldShowAssessmentHonestyNotice: Boolean,
    onAssessmentHonestyNoticeChanged: (Boolean) -> Unit
) {
    SettingsSectionCard(
        title = stringResource(R.string.settings_assessment_title),
        body = stringResource(R.string.settings_assessment_body)
    ) {
        SettingsSwitchRow(
            title = stringResource(R.string.settings_honesty_notice_title),
            body = stringResource(R.string.settings_honesty_notice_body),
            checked = shouldShowAssessmentHonestyNotice,
            onCheckedChange = onAssessmentHonestyNoticeChanged
        )
    }
}

@Composable
private fun MockHistorySection(
    isMockHistoryEnabled: Boolean,
    onMockHistoryEnabledChanged: (Boolean) -> Unit
) {
    SettingsSectionCard(
        title = stringResource(R.string.settings_mock_history_title),
        body = stringResource(R.string.settings_mock_history_body)
    ) {
        SettingsSwitchRow(
            title = stringResource(R.string.settings_mock_history_toggle_title),
            body = stringResource(
                if (isMockHistoryEnabled) {
                    R.string.settings_mock_history_toggle_body_on
                } else {
                    R.string.settings_mock_history_toggle_body_off
                }
            ),
            checked = isMockHistoryEnabled,
            rowTestTag = "settings_mock_history_row",
            switchTestTag = "settings_mock_history_switch",
            onCheckedChange = onMockHistoryEnabledChanged
        )
    }
}

@Composable
private fun OnboardingSection(
    onReplayOnboarding: () -> Unit
) {
    SettingsSectionCard(
        title = stringResource(R.string.settings_onboarding_title),
        body = stringResource(R.string.settings_onboarding_body)
    ) {
        SettingsActionRow(
            title = stringResource(R.string.settings_replay_onboarding_title),
            body = stringResource(R.string.settings_replay_onboarding_body),
            actionLabel = stringResource(R.string.settings_replay_onboarding_action),
            onAction = onReplayOnboarding
        )
    }
}

@Composable
private fun ObservabilityChecksSection(
    onReportTestNonFatal: () -> Unit,
    onForceTestCrash: () -> Unit
) {
    SettingsSectionCard(
        title = stringResource(R.string.settings_observability_checks_title),
        body = stringResource(R.string.settings_observability_checks_body)
    ) {
        SettingsActionRow(
            title = stringResource(R.string.settings_observability_nonfatal_title),
            body = stringResource(R.string.settings_observability_nonfatal_body),
            actionLabel = stringResource(R.string.settings_observability_nonfatal_action),
            buttonTestTag = "settings_observability_nonfatal_button",
            onAction = onReportTestNonFatal
        )
        SettingsActionRow(
            title = stringResource(R.string.settings_observability_crash_title),
            body = stringResource(R.string.settings_observability_crash_body),
            actionLabel = stringResource(R.string.settings_observability_crash_action),
            buttonTestTag = "settings_observability_crash_button",
            onAction = onForceTestCrash
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    body: String,
    content: @Composable ColumnScope.() -> Unit
) {
    AppSectionCard(
        title = title,
        body = body,
        showMarker = false,
        content = content
    )
}

@Composable
private fun ThemeModeOption(
    title: String,
    body: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    PreferenceSelectionRow(
        title = title,
        body = body,
        selected = selected,
        testTag = testTag,
        onClick = onClick
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    body: String,
    checked: Boolean,
    rowTestTag: String = "settings_honesty_row",
    switchTestTag: String = "settings_honesty_switch",
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceToggleRow(
        title = title,
        body = body,
        checked = checked,
        rowTestTag = rowTestTag,
        switchTestTag = switchTestTag,
        onCheckedChange = onCheckedChange
    )
}

@Composable
private fun SettingsActionRow(
    title: String,
    body: String,
    actionLabel: String,
    buttonTestTag: String = "settings_replay_onboarding_button",
    onAction: () -> Unit
) {
    PreferenceActionRow(
        title = title,
        body = body,
        actionLabel = actionLabel,
        buttonTestTag = buttonTestTag,
        onAction = onAction
    )
}
