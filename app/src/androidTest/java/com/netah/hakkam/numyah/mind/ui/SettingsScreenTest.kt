package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.AppThemeMode
import com.netah.hakkam.numyah.mind.ui.screen.SettingsScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.SettingsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.SettingsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_loadingState_displaysProgressIndicator() {
        composeTestRule.setContent {
            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = SettingsUiState.Loading,
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_loading_indicator").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_readyState_showsCoreSectionsAndAllowsThemeSelection() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var selectedThemeMode = AppThemeMode.SYSTEM

        composeTestRule.setContent {
            var uiState by remember {
                mutableStateOf(
                    SettingsUiState.Ready(
                        SettingsUiModel(
                            themeMode = AppThemeMode.SYSTEM,
                            shouldShowAssessmentHonestyNotice = true
                        )
                    )
                )
            }

            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = uiState,
                    onThemeModeSelected = {
                        selectedThemeMode = it
                        uiState = SettingsUiState.Ready(
                            SettingsUiModel(
                                themeMode = it,
                                shouldShowAssessmentHonestyNotice = true
                            )
                        )
                    },
                    onAssessmentHonestyNoticeChanged = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_appearance_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_assessment_title)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_scroll").performTouchInput { swipeUp() }
        composeTestRule.onNodeWithText(context.getString(R.string.settings_privacy_title)).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_theme_dark_title)
        ).performClick()
        composeTestRule.waitForIdle()

        assertEquals(AppThemeMode.DARK, selectedThemeMode)
    }

    @Test
    fun settingsScreen_toggleHonestyNotice_invokesCallback() {
        var honestyNoticeEnabled = true

        composeTestRule.setContent {
            var uiState by remember {
                mutableStateOf(
                    SettingsUiState.Ready(
                        SettingsUiModel(
                            themeMode = AppThemeMode.SYSTEM,
                            shouldShowAssessmentHonestyNotice = true
                        )
                    )
                )
            }

            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = uiState,
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {
                        honestyNoticeEnabled = it
                        uiState = SettingsUiState.Ready(
                            SettingsUiModel(
                                themeMode = AppThemeMode.SYSTEM,
                                shouldShowAssessmentHonestyNotice = it
                            )
                        )
                    },
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_honesty_switch").performClick()
        composeTestRule.waitForIdle()

        assertTrue(!honestyNoticeEnabled)
    }

    @Test
    fun settingsScreen_replayOnboardingFlow_invokesCallbackAfterConfirmation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var replayedOnboarding = false

        composeTestRule.setContent {
            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = SettingsUiState.Ready(
                        SettingsUiModel(
                            themeMode = AppThemeMode.SYSTEM,
                            shouldShowAssessmentHonestyNotice = true
                        )
                    ),
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onReplayOnboarding = { replayedOnboarding = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_scroll").performTouchInput { swipeUp() }
        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_replay_onboarding_action)
        ).performClick()
        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_replay_onboarding_confirm)
        ).performClick()

        assertTrue(replayedOnboarding)
    }
}
