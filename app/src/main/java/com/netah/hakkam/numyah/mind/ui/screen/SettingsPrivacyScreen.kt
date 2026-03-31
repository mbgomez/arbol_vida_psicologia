package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R

@Composable
fun SettingsPrivacyScreen(
    paddingValues: PaddingValues
) {
    SettingsDetailScaffold(paddingValues = paddingValues) {
        SettingsDetailHero(
            eyebrow = stringResource(R.string.settings_privacy_eyebrow),
            title = stringResource(R.string.settings_privacy_screen_title),
            body = stringResource(R.string.settings_privacy_screen_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_privacy_local_title),
            body = stringResource(R.string.settings_privacy_local_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_privacy_resume_title),
            body = stringResource(R.string.settings_privacy_resume_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_privacy_onboarding_title),
            body = stringResource(R.string.settings_privacy_onboarding_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_privacy_entry_note_title),
            body = stringResource(R.string.settings_privacy_entry_note_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_privacy_tester_telemetry_title),
            body = stringResource(R.string.settings_privacy_tester_telemetry_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_privacy_boundary_title),
            body = stringResource(R.string.settings_privacy_boundary_body)
        )
        SettingsDetailFooter(
            text = stringResource(R.string.settings_privacy_footer)
        )
    }
}
