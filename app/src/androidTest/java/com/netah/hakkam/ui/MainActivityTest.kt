package com.netah.hakkam.numyah.mind.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.model.Post
import com.netah.hakkam.numyah.mind.ui.screen.MainContent
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_MainContent_Loading() {
        val postListState: MutableState<List<Post>> = mutableStateOf(emptyList())
        val isLoadingState: MutableState<Boolean> = mutableStateOf(true)

        composeTestRule.setContent {
            AppTheme {
                MainContent(
                    postListState = postListState,
                    navigateToDetails = null,
                    isLoadingState = isLoadingState
                )
            }

        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val description = context.resources.getString(R.string.progress_indicator_desccription)
        composeTestRule.onNodeWithContentDescription(description).assertIsDisplayed()
    }

    @Test
    fun test_MainContent_Post_Collected() {
        val title1 = "title1"
        val body1 = "body1"
        val title2 = "title2"
        val body2 = "body2"

        val postUI1 = Post(title1, body1)
        val postUI2 = Post(title2, body2)

        val postList = listOf(postUI1, postUI2)

        val postListState: MutableState<List<Post>> = mutableStateOf(postList)
        val isLoadingState: MutableState<Boolean> = mutableStateOf(false)

        composeTestRule.setContent {

            AppTheme {
                MainContent(
                    postListState = postListState,
                    navigateToDetails = null,
                    isLoadingState = isLoadingState
                )
            }

        }

        composeTestRule.onNodeWithText(title1).assertIsDisplayed()
        composeTestRule.onNodeWithText(body1).assertIsDisplayed()
        composeTestRule.onNodeWithText(title2).assertIsDisplayed()
        composeTestRule.onNodeWithText(body2).assertIsDisplayed()
    }

}