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
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.AppLanguageMode
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
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_loading_indicator").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_readyState_showsCoreSections() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = readyState(),
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_appearance_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_language_title)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_scroll").performTouchInput { swipeUp() }
        composeTestRule.onNodeWithText(context.getString(R.string.settings_assessment_title)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_mock_history_title)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_privacy_title)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_about_title)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun settingsScreen_themeSelection_invokesCallback() {
        var selectedThemeMode = AppThemeMode.SYSTEM

        composeTestRule.setContent {
            var uiState by remember { mutableStateOf(readyState()) }

            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = uiState,
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {
                        selectedThemeMode = it
                        uiState = readyState(themeMode = it)
                    },
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_theme_dark").performClick()
        composeTestRule.waitForIdle()

        assertEquals(AppThemeMode.DARK, selectedThemeMode)
    }

    @Test
    fun settingsScreen_languageSelection_invokesCallback() {
        var selectedLanguageMode = AppLanguageMode.SYSTEM

        composeTestRule.setContent {
            var uiState by remember { mutableStateOf(readyState()) }

            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = uiState,
                    onLanguageModeSelected = {
                        selectedLanguageMode = it
                        uiState = readyState(languageMode = it)
                    },
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_language_spanish").performScrollTo()
        composeTestRule.onNodeWithTag("settings_language_spanish_radio").performClick()
        composeTestRule.waitForIdle()

        assertEquals(AppLanguageMode.SPANISH, selectedLanguageMode)
    }

    @Test
    fun settingsScreen_toggleHonestyNotice_invokesCallback() {
        var honestyNoticeEnabled = true

        composeTestRule.setContent {
            var uiState by remember { mutableStateOf(readyState()) }

            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = uiState,
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {
                        honestyNoticeEnabled = it
                        uiState = readyState(honestyNoticeVisible = it)
                    },
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_honesty_row").performScrollTo().performClick()
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
                    uiState = readyState(),
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = { replayedOnboarding = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_replay_onboarding_button").performScrollTo().performClick()
        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_replay_onboarding_confirm)
        ).performClick()

        assertTrue(replayedOnboarding)
    }

    @Test
    fun settingsScreen_openPrivacyAndAbout_invokeCallbacks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var openedPrivacy = false
        var openedAbout = false

        composeTestRule.setContent {
            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = readyState(),
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = { openedPrivacy = true },
                    onOpenAbout = { openedAbout = true },
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_privacy_title)).performScrollTo().performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_about_title)).performScrollTo().performClick()

        assertTrue(openedPrivacy)
        assertTrue(openedAbout)
    }

    @Test
    fun settingsScreen_mockHistoryConfirmation_invokesCallbackAfterConfirmation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var mockHistoryEnabled = false

        composeTestRule.setContent {
            var uiState by remember { mutableStateOf(readyState()) }

            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = uiState,
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {
                        mockHistoryEnabled = it
                        uiState = readyState(mockHistoryEnabled = it)
                    },
                    onReportTestNonFatal = {},
                    onForceTestCrash = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_mock_history_row").performScrollTo().performClick()
        composeTestRule.onNodeWithText(
            context.getString(R.string.settings_mock_history_dialog_confirm)
        ).performClick()

        assertTrue(mockHistoryEnabled)
    }

    @Test
    fun settingsScreen_observabilityChecks_invokeCallbacks() {
        var reportedNonFatal = false
        var forcedCrash = false

        composeTestRule.setContent {
            AppTheme {
                SettingsScreen(
                    paddingValues = PaddingValues(),
                    uiState = readyState(),
                    onLanguageModeSelected = {},
                    onThemeModeSelected = {},
                    onAssessmentHonestyNoticeChanged = {},
                    onMockHistoryEnabledChanged = {},
                    onReportTestNonFatal = { reportedNonFatal = true },
                    onForceTestCrash = { forcedCrash = true },
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onReplayOnboarding = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("settings_observability_nonfatal_button")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("settings_observability_crash_button")
            .performScrollTo()
            .performClick()

        assertTrue(reportedNonFatal)
        assertTrue(forcedCrash)
    }

    private fun readyState(
        languageMode: AppLanguageMode = AppLanguageMode.SYSTEM,
        themeMode: AppThemeMode = AppThemeMode.SYSTEM,
        honestyNoticeVisible: Boolean = true,
        mockHistoryEnabled: Boolean = false
    ): SettingsUiState {
        return SettingsUiState.Ready(
            SettingsUiModel(
                languageMode = languageMode,
                themeMode = themeMode,
                shouldShowAssessmentHonestyNotice = honestyNoticeVisible,
                showMockHistoryTools = true,
                isMockHistoryEnabled = mockHistoryEnabled
            )
        )
    }
}
