package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.CompletionPoleContent
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.ResponseScaleDefinition
import com.netah.hakkam.numyah.mind.domain.model.SavedResponse
import com.netah.hakkam.numyah.mind.domain.model.SephiraCompletionContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraDetailContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssessmentUiStateFactoryTests {

    @Test
    fun createPhaseState_whenHonestyNoticeNotDismissed_returnsHonestyNoticeState() {
        val state = AssessmentUiStateFactory.createPhaseState(
            questionnaire = questionnaire(),
            snapshot = snapshot(),
            honestyNoticeDismissed = false,
            doNotShowHonestyNoticeAgain = true,
            introDismissed = false
        )

        val honestyState = state as AssessmentUiState.HonestyNotice
        assertTrue(honestyState.model.isDoNotShowAgainChecked)
    }

    @Test
    fun createPhaseState_whenIntroActive_returnsIntroStateWithResumeFlag() {
        val state = AssessmentUiStateFactory.createPhaseState(
            questionnaire = questionnaire(),
            snapshot = snapshot(
                responses = listOf(
                    SavedResponse(
                        questionId = "q1",
                        selectedOptionId = "agree",
                        numericValue = 3,
                        questionOrder = 0,
                        answeredAt = 1L
                    )
                )
            ),
            honestyNoticeDismissed = true,
            doNotShowHonestyNoticeAgain = false,
            introDismissed = false
        )

        val introState = state as AssessmentUiState.Intro
        assertEquals("Malkuth", introState.model.sephiraName)
        assertTrue(introState.model.isResumeSession)
        assertEquals(2, introState.model.totalPages())
    }

    @Test
    fun createPhaseState_whenIntroDismissed_returnsQuestionStateWithSelectedOption() {
        val state = AssessmentUiStateFactory.createPhaseState(
            questionnaire = questionnaire(),
            snapshot = snapshot(
                currentPageIndex = 0,
                currentQuestionIndex = 1,
                responses = listOf(
                    SavedResponse(
                        questionId = "q2",
                        selectedOptionId = "agree",
                        numericValue = 3,
                        questionOrder = 1,
                        answeredAt = 1L
                    )
                )
            ),
            honestyNoticeDismissed = true,
            doNotShowHonestyNoticeAgain = false,
            introDismissed = true
        )

        val questionState = state as AssessmentUiState.Question
        assertEquals("Question 2", questionState.model.currentQuestionPrompt)
        assertEquals("agree", questionState.model.selectedOptionId)
        assertTrue(questionState.model.navigation.canContinue)
        assertEquals(2, questionState.model.progress.currentQuestionNumber)
    }

    @Test
    fun createCompletedState_returnsCompletedModelForDominantPoleAndNextSection() {
        val questionnaire = questionnaire()
        val nextSection = questionnaireWithTwoSections().sections.last()
        val state = AssessmentUiStateFactory.createCompletedState(
            questionnaire = questionnaireWithTwoSections(),
            snapshot = snapshot(
                questionnaireVersion = "assessment-v2",
                scores = listOf(
                    SephiraScore(
                        sessionId = 1L,
                        sephiraId = SephiraId.MALKUTH,
                        balanceScore = 0.2,
                        deficiencyScore = 0.2,
                        excessScore = 0.6,
                        dominantPole = Pole.EXCESS,
                        confidence = ConfidenceLevel.MEDIUM,
                        isLowConfidence = false
                    )
                )
            ),
            nextSection = nextSection
        )

        val completedState = state as AssessmentUiState.Completed
        assertEquals("Summary", completedState.model.sectionSummary)
        assertEquals("Excess reflection", completedState.model.completionReflection)
        assertEquals("Excess", completedState.model.dominantPattern)
        assertEquals("Excess practice", completedState.model.practiceSuggestion)
        assertTrue(completedState.model.hasNextSephira)
        assertEquals("Yesod", completedState.model.nextSephiraName)
    }

    @Test
    fun createCompletedState_returnsLowConfidenceFlagFromScore() {
        val state = AssessmentUiStateFactory.createCompletedState(
            questionnaire = questionnaire(),
            snapshot = snapshot(
                scores = listOf(
                    SephiraScore(
                        sessionId = 1L,
                        sephiraId = SephiraId.MALKUTH,
                        balanceScore = 0.45,
                        deficiencyScore = 0.30,
                        excessScore = 0.25,
                        dominantPole = Pole.BALANCE,
                        confidence = ConfidenceLevel.LOW,
                        isLowConfidence = true
                    )
                )
            ),
            nextSection = null
        )

        val completedState = state as AssessmentUiState.Completed
        assertFalse(completedState.model.hasNextSephira)
        assertTrue(completedState.model.isLowConfidence)
        assertEquals(ConfidenceLevel.LOW, completedState.model.confidence)
    }

    private fun questionnaire(): QuestionnaireContent {
        return QuestionnaireContent(
            version = "assessment-v1",
            title = "Assessment",
            responseScale = ResponseScaleDefinition(
                format = QuestionFormat.LIKERT_5,
                options = listOf(
                    AnswerOption("disagree", "Disagree", 1),
                    AnswerOption("agree", "Agree", 3)
                )
            ),
            sections = listOf(
                SephiraSectionContent(
                    sephiraId = SephiraId.MALKUTH,
                    displayName = "Malkuth",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    completionContent = testCompletionContent(),
                    detailContent = testDetailContent(),
                    pages = listOf(
                        QuestionPageContent("page_1", "Page 1", "Description 1", listOf("q1", "q2")),
                        QuestionPageContent("page_2", "Page 2", "Description 2", listOf("q3"))
                    ),
                    questions = listOf(
                        QuestionContent("q1", SephiraId.MALKUTH, "page_1", "Question 1", QuestionFormat.LIKERT_5, Pole.BALANCE),
                        QuestionContent("q2", SephiraId.MALKUTH, "page_1", "Question 2", QuestionFormat.LIKERT_5, Pole.DEFICIENCY),
                        QuestionContent("q3", SephiraId.MALKUTH, "page_2", "Question 3", QuestionFormat.LIKERT_5, Pole.EXCESS)
                    )
                )
            )
        )
    }

    private fun questionnaireWithTwoSections(): QuestionnaireContent {
        return QuestionnaireContent(
            version = "assessment-v2",
            title = "Assessment",
            responseScale = ResponseScaleDefinition(
                format = QuestionFormat.LIKERT_5,
                options = listOf(AnswerOption("agree", "Agree", 3))
            ),
            sections = listOf(
                questionnaire().sections.first(),
                SephiraSectionContent(
                    sephiraId = SephiraId.YESOD,
                    displayName = "Yesod",
                    shortMeaning = "Foundation",
                    introText = "Yesod intro",
                    completionContent = testCompletionContent(),
                    detailContent = testDetailContent(),
                    pages = listOf(QuestionPageContent("yesod_page", "Page", "Description", listOf("yesod_q1"))),
                    questions = listOf(
                        QuestionContent("yesod_q1", SephiraId.YESOD, "yesod_page", "Yesod Question", QuestionFormat.LIKERT_5, Pole.BALANCE)
                    )
                )
            )
        )
    }

    private fun snapshot(
        questionnaireVersion: String = "assessment-v1",
        currentPageIndex: Int = 0,
        currentQuestionIndex: Int = 0,
        responses: List<SavedResponse> = emptyList(),
        scores: List<SephiraScore> = emptyList()
    ): AssessmentSessionSnapshot {
        return AssessmentSessionSnapshot(
            sessionId = 1L,
            questionnaireVersion = questionnaireVersion,
            status = AssessmentStatus.IN_PROGRESS,
            currentSephiraId = SephiraId.MALKUTH,
            currentPageIndex = currentPageIndex,
            currentQuestionIndex = currentQuestionIndex,
            totalQuestions = 3,
            startedAt = 1L,
            completedAt = null,
            responses = responses,
            scores = scores
        )
    }

    private fun testDetailContent() = SephiraDetailContent(
        healthyExpression = "Healthy",
        deficiencyPattern = "Deficiency",
        excessPattern = "Excess",
        suggestedPractices = listOf("Practice")
    )

    private fun testCompletionContent() = SephiraCompletionContent(
        sectionSummary = "Summary",
        balanced = CompletionPoleContent(
            reflection = "Balanced reflection",
            practice = "Balanced practice"
        ),
        deficiency = CompletionPoleContent(
            reflection = "Deficiency reflection",
            practice = "Deficiency practice"
        ),
        excess = CompletionPoleContent(
            reflection = "Excess reflection",
            practice = "Excess practice"
        )
    )

    private fun AssessmentIntroUiModel.totalPages(): Int = progress.totalPages
}
