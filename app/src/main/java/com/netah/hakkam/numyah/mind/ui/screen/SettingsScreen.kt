package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.viewmodel.SettingsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.SettingsUiState

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    uiState: SettingsUiState,
    onLanguageModeSelected: (AppLanguageMode) -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onAssessmentHonestyNoticeChanged: (Boolean) -> Unit,
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
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onReplayOnboarding: () -> Unit
) {
    val horizontalPadding = dimensionResource(R.dimen.screen_padding_horizontal)
    val verticalPadding = dimensionResource(R.dimen.screen_padding_vertical)
    val sectionSpacing = dimensionResource(R.dimen.screen_section_spacing)

    var showReplayOnboardingDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_scroll")
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(sectionSpacing)
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
    val cardRadius = dimensionResource(R.dimen.radius_md)
    val cardElevation = dimensionResource(R.dimen.elevation_sm)
    val contentPadding = dimensionResource(R.dimen.spacing_xl)
    val rowSpacing = dimensionResource(R.dimen.screen_section_spacing)
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)
    val actionSpacing = dimensionResource(R.dimen.spacing_xs_plus)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = contentPadding, vertical = contentPadding),
            horizontalArrangement = Arrangement.spacedBy(rowSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(contentSpacing)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(actionSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsHero() {
    val cardRadius = dimensionResource(R.dimen.radius_lg)
    val horizontalPadding = dimensionResource(R.dimen.spacing_2xl)
    val verticalPadding = dimensionResource(R.dimen.spacing_xl)
    val sectionSpacing = dimensionResource(R.dimen.size_dot_sm)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
        ) {
            Text(
                text = stringResource(R.string.settings_eyebrow),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.settings_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
private fun SettingsSectionCard(
    title: String,
    body: String,
    content: @Composable () -> Unit
) {
    val cardRadius = dimensionResource(R.dimen.radius_md)
    val cardElevation = dimensionResource(R.dimen.elevation_sm)
    val contentPadding = dimensionResource(R.dimen.spacing_xl)
    val sectionSpacing = dimensionResource(R.dimen.screen_section_spacing)
    val textSpacing = dimensionResource(R.dimen.spacing_sm)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(textSpacing)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
private fun ThemeModeOption(
    title: String,
    body: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val optionRadius = dimensionResource(R.dimen.radius_sm)
    val borderWidth = dimensionResource(R.dimen.stroke_thin)
    val horizontalPadding = dimensionResource(R.dimen.onboarding_spacing_button)
    val verticalPadding = dimensionResource(R.dimen.onboarding_spacing_button)
    val contentSpacing = dimensionResource(R.dimen.onboarding_spacing_button)
    val textSpacing = dimensionResource(R.dimen.spacing_xs)
    val indicatorSize = dimensionResource(R.dimen.size_dot_sm)

    val borderColor = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(optionRadius)
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(optionRadius)
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier.testTag("${testTag}_radio"),
            selected = selected,
            onClick = onClick
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(textSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .background(
                    color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    body: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val rowRadius = dimensionResource(R.dimen.radius_sm)
    val horizontalPadding = dimensionResource(R.dimen.screen_section_spacing)
    val verticalPadding = dimensionResource(R.dimen.onboarding_spacing_button)
    val contentSpacing = dimensionResource(R.dimen.onboarding_spacing_button)
    val textSpacing = dimensionResource(R.dimen.spacing_xs)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_honesty_row")
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                shape = RoundedCornerShape(rowRadius)
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(textSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            modifier = Modifier.testTag("settings_honesty_switch"),
            checked = checked,
            onCheckedChange = null
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    val rowRadius = dimensionResource(R.dimen.radius_sm)
    val horizontalPadding = dimensionResource(R.dimen.screen_section_spacing)
    val verticalPadding = dimensionResource(R.dimen.screen_section_spacing)
    val contentSpacing = dimensionResource(R.dimen.onboarding_spacing_button)
    val textSpacing = dimensionResource(R.dimen.spacing_xs)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                shape = RoundedCornerShape(rowRadius)
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(textSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(
            modifier = Modifier.testTag("settings_replay_onboarding_button"),
            onClick = onAction
        ) {
            Text(text = actionLabel)
        }
    }
}
