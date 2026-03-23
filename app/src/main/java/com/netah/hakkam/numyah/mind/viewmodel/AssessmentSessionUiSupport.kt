package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.QuestionPageContent
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent

data class ActiveAssessmentUiModel(
    val sephiraName: String,
    val completedSephirotCount: Int,
    val totalSephirotCount: Int,
    val currentQuestionNumber: Int,
    val totalQuestions: Int,
    val isAtSectionStart: Boolean
)

internal fun buildActiveAssessmentUiModel(
    questionnaire: QuestionnaireContent,
    snapshot: AssessmentSessionSnapshot
): ActiveAssessmentUiModel? {
    val section = questionnaire.sections.firstOrNull { it.sephiraId == snapshot.currentSephiraId }
        ?: return null

    val safePageIndex = snapshot.currentPageIndex.coerceIn(0, section.pages.lastIndex)
    val page = section.pages.getOrNull(safePageIndex) ?: return null
    val safeQuestionIndex = snapshot.currentQuestionIndex.coerceIn(0, page.questionIds.lastIndex)
    val currentQuestionNumber = absoluteQuestionNumber(
        pages = section.pages,
        pageIndex = safePageIndex,
        questionIndex = safeQuestionIndex
    )
    val sectionQuestionIds = section.questions.map { it.id }.toSet()
    val hasSavedProgress = snapshot.currentPageIndex > 0 ||
        snapshot.currentQuestionIndex > 0 ||
        snapshot.responses.any { response -> response.questionId in sectionQuestionIds }

    return ActiveAssessmentUiModel(
        sephiraName = section.displayName,
        completedSephirotCount = snapshot.scores.size,
        totalSephirotCount = questionnaire.sections.size,
        currentQuestionNumber = currentQuestionNumber,
        totalQuestions = section.questions.size,
        isAtSectionStart = !hasSavedProgress
    )
}

private fun absoluteQuestionNumber(
    pages: List<QuestionPageContent>,
    pageIndex: Int,
    questionIndex: Int
): Int {
    val previousQuestions = pages.take(pageIndex).sumOf { it.questionIds.size }
    return previousQuestions + questionIndex + 1
}
