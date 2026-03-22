package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.BuildConfig
import com.netah.hakkam.numyah.mind.R

@Composable
fun SettingsAboutScreen(
    paddingValues: PaddingValues
) {
    SettingsDetailScaffold(paddingValues = paddingValues) {
        SettingsDetailHero(
            eyebrow = stringResource(R.string.settings_about_eyebrow),
            title = stringResource(R.string.settings_about_screen_title),
            body = stringResource(R.string.settings_about_screen_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_about_purpose_title),
            body = stringResource(R.string.settings_about_purpose_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_about_framework_title),
            body = stringResource(R.string.settings_about_framework_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_about_interpretation_title),
            body = stringResource(R.string.settings_about_interpretation_body)
        )
        SettingsDetailSection(
            title = stringResource(R.string.settings_about_version_title),
            body = stringResource(R.string.settings_about_version_body, BuildConfig.VERSION_NAME)
        )
        SettingsDetailFooter(
            text = stringResource(R.string.settings_about_footer)
        )
    }
}
