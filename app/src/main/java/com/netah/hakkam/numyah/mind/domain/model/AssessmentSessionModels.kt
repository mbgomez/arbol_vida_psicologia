package com.netah.hakkam.numyah.mind.domain.model

enum class AssessmentStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

enum class ConfidenceLevel {
    HIGH,
    MEDIUM,
    LOW
}

data class SavedResponse(
    val questionId: String,
    val selectedOptionId: String,
    val numericValue: Int,
    val questionOrder: Int,
    val answeredAt: Long
)

data class AssessmentSessionSnapshot(
    val sessionId: Long,
    val questionnaireVersion: String,
    val status: AssessmentStatus,
    val currentSephiraId: SephiraId,
    val currentPageIndex: Int,
    val currentQuestionIndex: Int,
    val totalQuestions: Int,
    val startedAt: Long,
    val completedAt: Long?,
    val responses: List<SavedResponse>,
    val scores: List<SephiraScore>
)

data class SephiraScore(
    val sessionId: Long,
    val sephiraId: SephiraId,
    val balanceScore: Double,
    val deficiencyScore: Double,
    val excessScore: Double,
    val dominantPole: Pole,
    val confidence: ConfidenceLevel,
    val isLowConfidence: Boolean
)
