package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.QuestionContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent

internal object AssessmentProgressionHelper {

    fun currentQuestion(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): QuestionContent? {
        val section = currentSection(questionnaire, snapshot) ?: return null
        val page = section.pages.getOrNull(snapshot.currentPageIndex) ?: return null
        val questionId = page.questionIds.getOrNull(snapshot.currentQuestionIndex) ?: return null
        return section.questions.firstOrNull { it.id == questionId }
    }

    fun nextPosition(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): AssessmentPosition {
        val section = currentSection(questionnaire, snapshot)
            ?: return AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex, true)
        val currentPage = section.pages.getOrNull(snapshot.currentPageIndex)
            ?: return AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex, true)
        return if (snapshot.currentQuestionIndex < currentPage.questionIds.lastIndex) {
            AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex + 1, false)
        } else if (snapshot.currentPageIndex < section.pages.lastIndex) {
            AssessmentPosition(snapshot.currentPageIndex + 1, 0, false)
        } else {
            AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex, true)
        }
    }

    fun previousPosition(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): AssessmentPosition? {
        val section = currentSection(questionnaire, snapshot) ?: return null
        return when {
            snapshot.currentQuestionIndex > 0 -> {
                AssessmentPosition(snapshot.currentPageIndex, snapshot.currentQuestionIndex - 1, false)
            }
            snapshot.currentPageIndex > 0 -> {
                val previousPageIndex = snapshot.currentPageIndex - 1
                val previousPage = section.pages[previousPageIndex]
                AssessmentPosition(previousPageIndex, previousPage.questionIds.lastIndex, false)
            }
            else -> null
        }
    }

    fun absoluteQuestionNumber(
        pages: List<QuestionPageContent>,
        pageIndex: Int,
        questionIndex: Int
    ): Int {
        val previousQuestions = pages.take(pageIndex).sumOf { it.questionIds.size }
        return previousQuestions + questionIndex + 1
    }

    fun currentSection(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot
    ): SephiraSectionContent? = questionnaire.sections.firstOrNull { it.sephiraId == activeSephira(snapshot) }

    fun hasProgressInCurrentSection(
        section: SephiraSectionContent,
        snapshot: AssessmentSessionSnapshot
    ): Boolean {
        if (snapshot.currentPageIndex > 0 || snapshot.currentQuestionIndex > 0) {
            return true
        }

        val sectionQuestionIds = section.questions.map { it.id }.toSet()
        return snapshot.responses.any { response ->
            response.questionId in sectionQuestionIds
        }
    }

    fun nextSection(
        questionnaire: QuestionnaireContent,
        currentSephiraId: SephiraId
    ): SephiraSectionContent? {
        val currentIndex = questionnaire.sections.indexOfFirst { it.sephiraId == currentSephiraId }
        if (currentIndex == -1) {
            return null
        }
        return questionnaire.sections.getOrNull(currentIndex + 1)
    }

    fun activeSephira(snapshot: AssessmentSessionSnapshot): SephiraId = snapshot.currentSephiraId
}

internal data class AssessmentPosition(
    val pageIndex: Int,
    val questionIndex: Int,
    val isComplete: Boolean
)
