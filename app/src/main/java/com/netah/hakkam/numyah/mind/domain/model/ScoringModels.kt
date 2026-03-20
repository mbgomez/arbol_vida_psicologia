package com.netah.hakkam.numyah.mind.domain.model

data class ScoreInput(
    val questionnaire: QuestionnaireContent,
    val sephiraId: SephiraId,
    val responses: List<SavedResponse>
)
