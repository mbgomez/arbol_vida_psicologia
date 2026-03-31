package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.model.AnswerOption
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.CompletionPoleContent
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
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AssessmentProgressionHelperTests {

    @Test
    fun currentQuestion_returnsQuestionForCurrentPageAndIndex() {
        val questionnaire = questionnaireWithTwoPages()
        val snapshot = testSnapshot(currentPageIndex = 1, currentQuestionIndex = 0)

        val question = AssessmentProgressionHelper.currentQuestion(questionnaire, snapshot)

        assertEquals("q3", question?.id)
    }

    @Test
    fun nextPosition_advancesWithinPageBeforeMovingPages() {
        val questionnaire = questionnaireWithTwoPages()
        val snapshot = testSnapshot(currentPageIndex = 0, currentQuestionIndex = 0)

        val next = AssessmentProgressionHelper.nextPosition(questionnaire, snapshot)

        assertEquals(0, next.pageIndex)
        assertEquals(1, next.questionIndex)
        assertFalse(next.isComplete)
    }

    @Test
    fun nextPosition_onLastQuestionOfPage_movesToNextPage() {
        val questionnaire = questionnaireWithTwoPages()
        val snapshot = testSnapshot(currentPageIndex = 0, currentQuestionIndex = 1)

        val next = AssessmentProgressionHelper.nextPosition(questionnaire, snapshot)

        assertEquals(1, next.pageIndex)
        assertEquals(0, next.questionIndex)
        assertFalse(next.isComplete)
    }

    @Test
    fun nextPosition_onLastQuestionOfLastPage_marksComplete() {
        val questionnaire = questionnaireWithTwoPages()
        val snapshot = testSnapshot(currentPageIndex = 1, currentQuestionIndex = 0)

        val next = AssessmentProgressionHelper.nextPosition(questionnaire, snapshot)

        assertEquals(1, next.pageIndex)
        assertEquals(0, next.questionIndex)
        assertTrue(next.isComplete)
    }

    @Test
    fun previousPosition_fromFirstQuestionOfSecondPage_returnsLastQuestionOfPreviousPage() {
        val questionnaire = questionnaireWithTwoPages()
        val snapshot = testSnapshot(currentPageIndex = 1, currentQuestionIndex = 0)

        val previous = AssessmentProgressionHelper.previousPosition(questionnaire, snapshot)

        assertEquals(0, previous?.pageIndex)
        assertEquals(1, previous?.questionIndex)
        assertFalse(previous?.isComplete ?: true)
    }

    @Test
    fun previousPosition_fromFirstQuestionOfFirstPage_returnsNull() {
        val questionnaire = questionnaireWithTwoPages()
        val snapshot = testSnapshot(currentPageIndex = 0, currentQuestionIndex = 0)

        val previous = AssessmentProgressionHelper.previousPosition(questionnaire, snapshot)

        assertNull(previous)
    }

    @Test
    fun absoluteQuestionNumber_countsAcrossPages() {
        val questionNumber = AssessmentProgressionHelper.absoluteQuestionNumber(
            pages = questionnaireWithTwoPages().sections.first().pages,
            pageIndex = 1,
            questionIndex = 0
        )

        assertEquals(3, questionNumber)
    }

    @Test
    fun hasProgressInCurrentSection_returnsFalseWhenOnlyPreviousSectionHasResponses() {
        val questionnaire = questionnaireWithTwoSections()
        val currentSection = questionnaire.sections.last()
        val snapshot = testSnapshot(
            questionnaireVersion = questionnaire.version,
            currentSephiraId = SephiraId.YESOD,
            responses = listOf(
                SavedResponse(
                    questionId = "malkuth_q1",
                    selectedOptionId = "agree",
                    numericValue = 3,
                    questionOrder = 0,
                    answeredAt = 1L
                )
            )
        )

        val hasProgress = AssessmentProgressionHelper.hasProgressInCurrentSection(currentSection, snapshot)

        assertFalse(hasProgress)
    }

    @Test
    fun hasProgressInCurrentSection_returnsTrueWhenCurrentSectionHasResponse() {
        val questionnaire = questionnaireWithTwoSections()
        val currentSection = questionnaire.sections.last()
        val snapshot = testSnapshot(
            questionnaireVersion = questionnaire.version,
            currentSephiraId = SephiraId.YESOD,
            responses = listOf(
                SavedResponse(
                    questionId = "yesod_q1",
                    selectedOptionId = "agree",
                    numericValue = 3,
                    questionOrder = 0,
                    answeredAt = 1L
                )
            )
        )

        val hasProgress = AssessmentProgressionHelper.hasProgressInCurrentSection(currentSection, snapshot)

        assertTrue(hasProgress)
    }

    @Test
    fun nextSection_returnsFollowingSection() {
        val questionnaire = questionnaireWithTwoSections()

        val nextSection = AssessmentProgressionHelper.nextSection(questionnaire, SephiraId.MALKUTH)

        assertEquals(SephiraId.YESOD, nextSection?.sephiraId)
    }

    private fun questionnaireWithTwoPages(): QuestionnaireContent {
        return QuestionnaireContent(
            version = "assessment-v1",
            title = "Assessment",
            responseScale = ResponseScaleDefinition(
                format = QuestionFormat.LIKERT_5,
                options = listOf(AnswerOption("agree", "Agree", 3))
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
                SephiraSectionContent(
                    sephiraId = SephiraId.MALKUTH,
                    displayName = "Malkuth",
                    shortMeaning = "Meaning",
                    introText = "Intro",
                    completionContent = testCompletionContent(),
                    detailContent = testDetailContent(),
                    pages = listOf(QuestionPageContent("malkuth_page", "Page", "Description", listOf("malkuth_q1"))),
                    questions = listOf(
                        QuestionContent("malkuth_q1", SephiraId.MALKUTH, "malkuth_page", "Malkuth Question", QuestionFormat.LIKERT_5, Pole.BALANCE)
                    )
                ),
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

    private fun testSnapshot(
        questionnaireVersion: String = "assessment-v1",
        currentSephiraId: SephiraId = SephiraId.MALKUTH,
        currentPageIndex: Int = 0,
        currentQuestionIndex: Int = 0,
        responses: List<SavedResponse> = emptyList()
    ): AssessmentSessionSnapshot {
        return AssessmentSessionSnapshot(
            sessionId = 1L,
            questionnaireVersion = questionnaireVersion,
            status = AssessmentStatus.IN_PROGRESS,
            currentSephiraId = currentSephiraId,
            currentPageIndex = currentPageIndex,
            currentQuestionIndex = currentQuestionIndex,
            totalQuestions = 3,
            startedAt = 1L,
            completedAt = null,
            responses = responses,
            scores = emptyList()
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
}
