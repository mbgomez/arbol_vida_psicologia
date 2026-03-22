package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AssessmentSessionRepository
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

data class StartOrResumeAssessmentParams(
    val questionnaireVersion: String,
    val initialSephiraId: SephiraId,
    val totalQuestions: Int
)

data class SaveAnswerParams(
    val sessionId: Long,
    val questionId: String,
    val selectedOptionId: String,
    val numericValue: Int,
    val questionOrder: Int,
    val nextPageIndex: Int,
    val nextQuestionIndex: Int
)

data class UpdateAssessmentProgressParams(
    val sessionId: Long,
    val pageIndex: Int,
    val questionIndex: Int
)

data class AdvanceAssessmentSectionParams(
    val sessionId: Long,
    val sephiraId: SephiraId,
    val totalQuestions: Int
)

class StartOrResumeAssessmentUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractor<StartOrResumeAssessmentParams, AssessmentSessionSnapshot>() {
    override fun buildUseCase(params: StartOrResumeAssessmentParams): Flow<AssessmentSessionSnapshot> {
        return assessmentSessionRepository.startOrResumeSession(
            questionnaireVersion = params.questionnaireVersion,
            initialSephiraId = params.initialSephiraId,
            totalQuestions = params.totalQuestions
        )
    }
}

class ObserveActiveAssessmentUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractorNoParams<AssessmentSessionSnapshot?>() {
    override fun buildUseCase(): Flow<AssessmentSessionSnapshot?> {
        return assessmentSessionRepository.observeActiveSession()
    }
}

class ObserveLatestCompletedAssessmentUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractorNoParams<AssessmentSessionSnapshot?>() {
    override fun buildUseCase(): Flow<AssessmentSessionSnapshot?> {
        return assessmentSessionRepository.observeLatestCompletedSession()
    }
}

class ObserveAssessmentHistoryUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractorNoParams<List<AssessmentSessionSnapshot>>() {
    override fun buildUseCase(): Flow<List<AssessmentSessionSnapshot>> {
        return assessmentSessionRepository.observeCompletedSessions()
    }
}

class ObserveCompletedAssessmentByIdUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractor<Long, AssessmentSessionSnapshot?>() {
    override fun buildUseCase(params: Long): Flow<AssessmentSessionSnapshot?> {
        return assessmentSessionRepository.observeCompletedSession(params)
    }
}

class SaveAssessmentAnswerUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractor<SaveAnswerParams, AssessmentSessionSnapshot>() {
    override fun buildUseCase(params: SaveAnswerParams): Flow<AssessmentSessionSnapshot> {
        return assessmentSessionRepository.saveAnswer(
            sessionId = params.sessionId,
            questionId = params.questionId,
            selectedOptionId = params.selectedOptionId,
            numericValue = params.numericValue,
            questionOrder = params.questionOrder,
            nextPageIndex = params.nextPageIndex,
            nextQuestionIndex = params.nextQuestionIndex
        )
    }
}

class UpdateAssessmentProgressUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractor<UpdateAssessmentProgressParams, AssessmentSessionSnapshot>() {
    override fun buildUseCase(params: UpdateAssessmentProgressParams): Flow<AssessmentSessionSnapshot> {
        return assessmentSessionRepository.updateProgress(
            sessionId = params.sessionId,
            pageIndex = params.pageIndex,
            questionIndex = params.questionIndex
        )
    }
}

class SaveAssessmentScoreUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractor<Pair<Long, SephiraScore>, AssessmentSessionSnapshot>() {
    override fun buildUseCase(params: Pair<Long, SephiraScore>): Flow<AssessmentSessionSnapshot> {
        return assessmentSessionRepository.saveSephiraScore(
            sessionId = params.first,
            score = params.second
        )
    }
}

class AdvanceAssessmentSectionUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractor<AdvanceAssessmentSectionParams, AssessmentSessionSnapshot>() {
    override fun buildUseCase(params: AdvanceAssessmentSectionParams): Flow<AssessmentSessionSnapshot> {
        return assessmentSessionRepository.advanceToSephira(
            sessionId = params.sessionId,
            sephiraId = params.sephiraId,
            totalQuestions = params.totalQuestions
        )
    }
}

class CompleteAssessmentUseCase @Inject constructor(
    private val assessmentSessionRepository: AssessmentSessionRepository
) : FlowInteractor<Pair<Long, SephiraScore>, AssessmentSessionSnapshot>() {
    override fun buildUseCase(params: Pair<Long, SephiraScore>): Flow<AssessmentSessionSnapshot> {
        return assessmentSessionRepository.completeSession(
            sessionId = params.first,
            score = params.second
        )
    }
}
