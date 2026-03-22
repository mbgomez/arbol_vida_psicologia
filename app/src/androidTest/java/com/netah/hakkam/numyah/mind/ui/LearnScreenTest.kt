package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.screen.LEARN_CATALOG_SCROLL_TAG
import com.netah.hakkam.numyah.mind.ui.screen.LEARN_COURSE_SCROLL_TAG
import com.netah.hakkam.numyah.mind.ui.screen.LEARN_MARK_SECTION_COMPLETED_BUTTON_TAG
import com.netah.hakkam.numyah.mind.ui.screen.LearnCourseScreen
import com.netah.hakkam.numyah.mind.ui.screen.LearnScreen
import com.netah.hakkam.numyah.mind.ui.screen.LearnSectionScreen
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.LearnCatalogUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseCardUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionListItemUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LearnScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun learnScreen_loadedState_rendersSeededCourseAndOpensIt() {
        var openedCourseId: String? = null

        composeTestRule.setContent {
            AppTheme {
                LearnScreen(
                    paddingValues = PaddingValues(),
                    uiState = LearnUiState.Loaded(learnCatalogModel()),
                    onRetry = {},
                    onOpenCourse = { openedCourseId = it }
                )
            }
        }

        composeTestRule.onNodeWithTag(LEARN_CATALOG_SCROLL_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithTag("learn_course_card_tree-of-life-overview").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tree of Life overview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("learn_course_card_tree-of-life-overview").performClick()

        assertEquals("tree-of-life-overview", openedCourseId)
    }

    @Test
    fun learnCourseScreen_showsAvailableAndLockedSections() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                LearnCourseScreen(
                    paddingValues = PaddingValues(),
                    uiState = LearnCourseUiState.Loaded(learnCourseModel()),
                    onRetry = {},
                    onOpenSection = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithTag(LEARN_COURSE_SCROLL_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText("1. Introduction").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.learn_section_status_available)).assertIsDisplayed()
        composeTestRule.onNodeWithText("2. Malkuth").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.learn_section_status_locked)).assertIsDisplayed()
    }

    @Test
    fun learnSectionScreen_rendersChapterTitleBodyAndCompletionAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var markedCompleted = false

        composeTestRule.setContent {
            AppTheme {
                LearnSectionScreen(
                    paddingValues = PaddingValues(),
                    uiState = LearnSectionUiState.Loaded(unfinishedSectionModel()),
                    onRetry = {},
                    onOpenSection = { _, _ -> },
                    onOpenCourse = {},
                    onMarkSectionCompleted = { markedCompleted = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Introduction").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tree of Life overview").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "A first approach to the Tree of Life as one of the central tools of Kabbalah."
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LEARN_MARK_SECTION_COMPLETED_BUTTON_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.learn_reader_footer_unfinished)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LEARN_MARK_SECTION_COMPLETED_BUTTON_TAG).performClick()

        assertTrue(markedCompleted)
    }

    @Test
    fun learnSectionScreen_completedState_showsNextChapterActionWhenUnlocked() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var openedSection: Pair<String, String>? = null
        var openedCourse: String? = null

        composeTestRule.setContent {
            AppTheme {
                LearnSectionScreen(
                    paddingValues = PaddingValues(),
                    uiState = LearnSectionUiState.Loaded(completedSectionModel()),
                    onRetry = {},
                    onOpenSection = { courseId, sectionId -> openedSection = courseId to sectionId },
                    onOpenCourse = { openedCourse = it },
                    onMarkSectionCompleted = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(LEARN_MARK_SECTION_COMPLETED_BUTTON_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithText(
            context.getString(R.string.learn_previous_section_action, "Introduction")
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.learn_next_section_action, "Yesod")
        ).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(
            context.getString(R.string.learn_reader_footer_completed)
        ).assertIsDisplayed()

        assertEquals("tree-of-life-overview" to "yesod", openedSection)
        assertEquals(null, openedCourse)
    }

    @Test
    fun learnSectionScreen_lastAvailableChapter_showsReturnToCourseAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var openedCourse: String? = null

        composeTestRule.setContent {
            AppTheme {
                LearnSectionScreen(
                    paddingValues = PaddingValues(),
                    uiState = LearnSectionUiState.Loaded(lastCompletedSectionModel()),
                    onRetry = {},
                    onOpenSection = { _, _ -> },
                    onOpenCourse = { openedCourse = it },
                    onMarkSectionCompleted = {}
                )
            }
        }

        composeTestRule.onNodeWithText(
            context.getString(R.string.learn_back_to_course_action)
        ).assertIsDisplayed().performClick()

        assertEquals("tree-of-life-overview", openedCourse)
    }

    @Test
    fun learnSectionScreen_lockedState_showsLockedSurface() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            AppTheme {
                LearnSectionScreen(
                    paddingValues = PaddingValues(),
                    uiState = LearnSectionUiState.Locked,
                    onRetry = {},
                    onOpenSection = { _, _ -> },
                    onOpenCourse = {},
                    onMarkSectionCompleted = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText(context.getString(R.string.learn_locked_title)).assertCountEquals(2)
        composeTestRule.onNodeWithText(context.getString(R.string.learn_locked_body)).assertIsDisplayed()
    }

    private fun learnCatalogModel(): LearnCatalogUiModel {
        return LearnCatalogUiModel(
            title = "Courses",
            courses = listOf(
                LearnCourseCardUiModel(
                    id = "tree-of-life-overview",
                    title = "Tree of Life overview",
                    subtitle = "A first approach to the Tree of Life and its ten sephirot",
                    description = "An introductory reading path through the Tree of Life as a Kabbalistic map of the universe and of the psyche.",
                    estimatedMinutes = 34,
                    availableSectionCount = 3,
                    totalSectionCount = 11
                )
            )
        )
    }

    private fun learnCourseModel(): LearnCourseUiModel {
        return LearnCourseUiModel(
            id = "tree-of-life-overview",
            title = "Tree of Life overview",
            subtitle = "A first approach to the Tree of Life and its ten sephirot",
            description = "An introductory reading path through the Tree of Life as a Kabbalistic map of the universe and of the psyche.",
            estimatedMinutes = 34,
            availableSectionCount = 3,
            totalSectionCount = 11,
            sections = listOf(
                LearnSectionListItemUiModel(
                    id = "introduction",
                    title = "Introduction",
                    summary = "A first approach to the Tree of Life as one of the central tools of Kabbalah.",
                    readingTimeMinutes = 13,
                    order = 1,
                    isCompleted = false,
                    isLocked = false
                ),
                LearnSectionListItemUiModel(
                    id = "malkuth",
                    title = "Malkuth",
                    summary = "The kingdom, the realm of matter, and practical life through the middah and the two basic klippot.",
                    readingTimeMinutes = 11,
                    order = 2,
                    isCompleted = false,
                    isLocked = true
                )
            )
        )
    }

    private fun unfinishedSectionModel(): LearnSectionUiModel {
        return LearnSectionUiModel(
            courseId = "tree-of-life-overview",
            courseTitle = "Tree of Life overview",
            sectionId = "introduction",
            sectionTitle = "Introduction",
            summary = "A first approach to the Tree of Life as one of the central tools of Kabbalah.",
            order = 1,
            totalAvailableSections = 3,
            readingTimeMinutes = 13,
            paragraphs = listOf(
                "Before we do a dive into the mysteries of Kabbalah, I would like to start by talking about one of the most important tools: the Tree of Life.",
                "The ten sephirot are connected through these channels, and each sephira represents a different energy or vibration."
            ),
            isCompleted = false,
            previousSectionId = null,
            previousSectionTitle = null,
            nextSectionId = "malkuth",
            nextSectionTitle = "Malkuth",
            isNextSectionLocked = true
        )
    }

    private fun completedSectionModel(): LearnSectionUiModel {
        return LearnSectionUiModel(
            courseId = "tree-of-life-overview",
            courseTitle = "Tree of Life overview",
            sectionId = "malkuth",
            sectionTitle = "Malkuth",
            summary = "The kingdom, the realm of matter, and the way we relate to practical reality.",
            order = 2,
            totalAvailableSections = 3,
            readingTimeMinutes = 11,
            paragraphs = listOf(
                "This sephira represents the energy of matter.",
                "The balanced point is not suppression of growth. It is remembering that material increase should not become the final objective of existence."
            ),
            isCompleted = true,
            previousSectionId = "introduction",
            previousSectionTitle = "Introduction",
            nextSectionId = "yesod",
            nextSectionTitle = "Yesod",
            isNextSectionLocked = false
        )
    }

    private fun lastCompletedSectionModel(): LearnSectionUiModel {
        return LearnSectionUiModel(
            courseId = "tree-of-life-overview",
            courseTitle = "Tree of Life overview",
            sectionId = "yesod",
            sectionTitle = "Yesod",
            summary = "Foundation, the social self, ego, self-esteem, and the balance between being with others and being with oneself.",
            order = 3,
            totalAvailableSections = 3,
            readingTimeMinutes = 12,
            paragraphs = listOf(
                "The first question we could ask is why this sephira is called Foundation.",
                "The important lesson here is to keep balance."
            ),
            isCompleted = true,
            previousSectionId = "malkuth",
            previousSectionTitle = "Malkuth",
            nextSectionId = null,
            nextSectionTitle = null,
            isNextSectionLocked = false
        )
    }
}
