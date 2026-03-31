package com.netah.hakkam.numyah.mind.viewmodel

import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import com.netah.hakkam.numyah.mind.domain.model.SephiraSectionContent

internal object AssessmentUiStateFactory {

    fun createPhaseState(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot,
        honestyNoticeDismissed: Boolean,
        doNotShowHonestyNoticeAgain: Boolean,
        introDismissed: Boolean
    ): AssessmentUiState? {
        val section = AssessmentProgressionHelper.currentSection(questionnaire, snapshot) ?: return null

        if (!honestyNoticeDismissed) {
            return AssessmentUiState.HonestyNotice(
                AssessmentHonestyNoticeUiModel(
                    isDoNotShowAgainChecked = doNotShowHonestyNoticeAgain
                )
            )
        }

        if (!introDismissed) {
            val hasSectionProgress = AssessmentProgressionHelper.hasProgressInCurrentSection(section, snapshot)
            return AssessmentUiState.Intro(
                AssessmentIntroUiModel(
                    questionnaireTitle = questionnaire.title,
                    sephiraId = section.sephiraId,
                    sephiraName = section.displayName,
                    shortMeaning = section.shortMeaning,
                    introText = section.introText,
                    isResumeSession = hasSectionProgress,
                    progress = AssessmentProgressUiModel(
                        currentPageIndex = snapshot.currentPageIndex,
                        totalPages = section.pages.size,
                        currentQuestionNumber = 0,
                        totalQuestions = section.questions.size,
                        overallProgress = 0f
                    )
                )
            )
        }

        return createQuestionState(questionnaire, snapshot, section)
    }

    fun createCompletedState(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot,
        nextSection: SephiraSectionContent?
    ): AssessmentUiState.Completed? {
        val activeSephira = AssessmentProgressionHelper.activeSephira(snapshot)
        val section = questionnaire.sections.firstOrNull { it.sephiraId == activeSephira } ?: return null
        val score = snapshot.scores.firstOrNull { it.sephiraId == activeSephira } ?: return null
        val completionState = when (score.dominantPole) {
            Pole.BALANCE -> section.completionContent.balanced
            Pole.DEFICIENCY -> section.completionContent.deficiency
            Pole.EXCESS -> section.completionContent.excess
        }

        return AssessmentUiState.Completed(
            AssessmentCompletedUiModel(
                sephiraId = section.sephiraId,
                sephiraName = section.displayName,
                sectionSummary = section.completionContent.sectionSummary,
                completionReflection = completionState.reflection,
                practiceSuggestion = completionState.practice,
                dominantPole = score.dominantPole,
                confidence = score.confidence,
                balanceScore = score.balanceScore,
                deficiencyScore = score.deficiencyScore,
                excessScore = score.excessScore,
                isLowConfidence = score.isLowConfidence,
                hasNextSephira = nextSection != null,
                nextSephiraName = nextSection?.displayName
            )
        )
    }

    private fun createQuestionState(
        questionnaire: QuestionnaireContent,
        snapshot: AssessmentSessionSnapshot,
        section: SephiraSectionContent
    ): AssessmentUiState.Question? {
        val page = section.pages.getOrNull(snapshot.currentPageIndex) ?: section.pages.firstOrNull() ?: return null
        val questionId = page.questionIds.getOrNull(snapshot.currentQuestionIndex) ?: page.questionIds.firstOrNull()
        val question = section.questions.firstOrNull { it.id == questionId } ?: return null
        val selectedResponse = snapshot.responses.firstOrNull { it.questionId == question.id }
        val questionNumber = AssessmentProgressionHelper.absoluteQuestionNumber(
            pages = section.pages,
            pageIndex = snapshot.currentPageIndex,
            questionIndex = snapshot.currentQuestionIndex
        )
        val totalQuestions = section.questions.size

        return AssessmentUiState.Question(
            AssessmentQuestionUiModel(
                questionnaireTitle = questionnaire.title,
                sephiraId = section.sephiraId,
                sephiraName = section.displayName,
                currentPageTitle = page.title,
                currentPageDescription = page.description,
                currentQuestionPrompt = question.prompt,
                answerOptions = questionnaire.responseScale.options.map { option ->
                    AssessmentAnswerOptionUiModel(
                        id = option.id,
                        label = option.label,
                        numericValue = option.numericValue,
                        isSelected = option.id == selectedResponse?.selectedOptionId
                    )
                },
                selectedOptionId = selectedResponse?.selectedOptionId,
                progress = AssessmentProgressUiModel(
                    currentPageIndex = snapshot.currentPageIndex,
                    totalPages = section.pages.size,
                    currentQuestionNumber = questionNumber,
                    totalQuestions = totalQuestions,
                    overallProgress = questionNumber.toFloat() / totalQuestions.toFloat()
                ),
                navigation = AssessmentNavigationUiModel(
                    canGoBack = questionNumber > 1,
                    canContinue = selectedResponse != null,
                    isFirstQuestion = questionNumber == 1,
                    isLastQuestion = questionNumber == totalQuestions
                )
            )
        )
    }
}
