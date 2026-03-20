package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AssessmentContentRepository
import com.netah.hakkam.numyah.mind.domain.model.QuestionnaireContent
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetCurrentQuestionnaireUseCase @Inject constructor(
    private val assessmentContentRepository: AssessmentContentRepository
) : FlowInteractor<Locale, QuestionnaireContent>() {
    override fun buildUseCase(params: Locale): Flow<QuestionnaireContent> {
        return assessmentContentRepository.getCurrentQuestionnaire(params)
    }
}
