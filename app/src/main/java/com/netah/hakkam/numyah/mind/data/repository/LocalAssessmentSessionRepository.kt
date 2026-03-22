package com.netah.hakkam.numyah.mind.data.repository

import com.netah.hakkam.numyah.mind.data.local.database.AssessmentSessionDao
import com.netah.hakkam.numyah.mind.data.local.database.AssessmentSessionTable
import com.netah.hakkam.numyah.mind.data.local.database.ResponseTable
import com.netah.hakkam.numyah.mind.data.local.database.SephiraScoreTable
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.SavedResponse
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface AssessmentSessionRepository {
    fun startOrResumeSession(
        questionnaireVersion: String,
        initialSephiraId: SephiraId,
        totalQuestions: Int
    ): Flow<AssessmentSessionSnapshot>

    fun observeActiveSession(): Flow<AssessmentSessionSnapshot?>

    fun observeLatestCompletedSession(): Flow<AssessmentSessionSnapshot?>

    fun saveAnswer(
        sessionId: Long,
        questionId: String,
        selectedOptionId: String,
        numericValue: Int,
        questionOrder: Int,
        nextPageIndex: Int,
        nextQuestionIndex: Int
    ): Flow<AssessmentSessionSnapshot>

    fun updateProgress(
        sessionId: Long,
        pageIndex: Int,
        questionIndex: Int
    ): Flow<AssessmentSessionSnapshot>

    fun saveSephiraScore(
        sessionId: Long,
        score: SephiraScore
    ): Flow<AssessmentSessionSnapshot>

    fun advanceToSephira(
        sessionId: Long,
        sephiraId: SephiraId,
        totalQuestions: Int
    ): Flow<AssessmentSessionSnapshot>

    fun completeSession(
        sessionId: Long,
        score: SephiraScore
    ): Flow<AssessmentSessionSnapshot>
}

class LocalAssessmentSessionRepository @Inject constructor(
    private val assessmentSessionDao: AssessmentSessionDao
) : AssessmentSessionRepository {

    override fun startOrResumeSession(
        questionnaireVersion: String,
        initialSephiraId: SephiraId,
        totalQuestions: Int
    ): Flow<AssessmentSessionSnapshot> = flow {
        val existingSession = assessmentSessionDao.getActiveInProgressSession()
        val sessionId = if (existingSession != null) {
            existingSession.id
        } else {
            assessmentSessionDao.deactivateActiveSessions()
            assessmentSessionDao.insertSession(
                AssessmentSessionTable(
                    questionnaireVersion = questionnaireVersion,
                    status = AssessmentStatus.IN_PROGRESS,
                    currentSephiraId = initialSephiraId,
                    currentPageIndex = 0,
                    currentQuestionIndex = 0,
                    totalQuestions = totalQuestions,
                    startedAt = System.currentTimeMillis(),
                    isActive = true
                )
            )
        }

        val session = assessmentSessionDao.getActiveInProgressSession()
            ?: error("Expected an active assessment session after startOrResumeSession")
        val responses = assessmentSessionDao.getResponsesForSession(sessionId)
        emit(session.toSnapshot(responses = responses, scores = emptyList()))
    }

    override fun observeActiveSession(): Flow<AssessmentSessionSnapshot?> {
        return assessmentSessionDao.observeActiveInProgressSession().flatMapLatest { session ->
            if (session == null) {
                flowOf(null)
            } else {
                combine(
                    assessmentSessionDao.observeResponses(session.id),
                    assessmentSessionDao.observeScores(session.id)
                ) { responses, scores ->
                    session.toSnapshot(
                        responses = responses,
                        scores = scores
                    )
                }
            }
        }
    }

    override fun observeLatestCompletedSession(): Flow<AssessmentSessionSnapshot?> {
        return assessmentSessionDao.observeLatestCompletedSession().flatMapLatest { session ->
            if (session == null) {
                flowOf(null)
            } else {
                combine(
                    assessmentSessionDao.observeResponses(session.id),
                    assessmentSessionDao.observeScores(session.id)
                ) { responses, scores ->
                    session.toSnapshot(
                        responses = responses,
                        scores = scores
                    )
                }
            }
        }
    }

    override fun saveAnswer(
        sessionId: Long,
        questionId: String,
        selectedOptionId: String,
        numericValue: Int,
        questionOrder: Int,
        nextPageIndex: Int,
        nextQuestionIndex: Int
    ): Flow<AssessmentSessionSnapshot> = flow {
        assessmentSessionDao.insertResponse(
            ResponseTable(
                sessionId = sessionId,
                questionId = questionId,
                selectedOptionId = selectedOptionId,
                numericValue = numericValue,
                questionOrder = questionOrder,
                answeredAt = System.currentTimeMillis()
            )
        )
        assessmentSessionDao.updateProgress(
            sessionId = sessionId,
            pageIndex = nextPageIndex,
            questionIndex = nextQuestionIndex
        )
        val session = assessmentSessionDao.getActiveInProgressSession()
            ?: error("Expected active session after saving answer")
        val responses = assessmentSessionDao.getResponsesForSession(sessionId)
        emit(session.toSnapshot(responses = responses, scores = emptyList()))
    }

    override fun completeSession(
        sessionId: Long,
        score: SephiraScore
    ): Flow<AssessmentSessionSnapshot> = flow {
        assessmentSessionDao.insertScore(score.toTable())
        assessmentSessionDao.updateCompletion(
            sessionId = sessionId,
            status = AssessmentStatus.COMPLETED,
            completedAt = System.currentTimeMillis(),
            isActive = false
        )
        val completedSession = assessmentSessionDao.getSessionById(sessionId)
            ?: error("Expected completed session to exist after completion")
        val responses = assessmentSessionDao.getResponsesForSession(sessionId)
        val scores = assessmentSessionDao.getScoresForSession(sessionId)
        emit(completedSession.toSnapshot(responses = responses, scores = scores))
    }

    override fun saveSephiraScore(
        sessionId: Long,
        score: SephiraScore
    ): Flow<AssessmentSessionSnapshot> = flow {
        assessmentSessionDao.insertScore(score.toTable())
        val session = assessmentSessionDao.getSessionById(sessionId)
            ?: error("Expected session after saving sephira score")
        val responses = assessmentSessionDao.getResponsesForSession(sessionId)
        val scores = assessmentSessionDao.getScoresForSession(sessionId)
        emit(session.toSnapshot(responses = responses, scores = scores))
    }

    override fun advanceToSephira(
        sessionId: Long,
        sephiraId: SephiraId,
        totalQuestions: Int
    ): Flow<AssessmentSessionSnapshot> = flow {
        assessmentSessionDao.advanceToSephira(
            sessionId = sessionId,
            sephiraId = sephiraId,
            pageIndex = 0,
            questionIndex = 0,
            totalQuestions = totalQuestions
        )
        val session = assessmentSessionDao.getSessionById(sessionId)
            ?: error("Expected session after advancing to sephira")
        val responses = assessmentSessionDao.getResponsesForSession(sessionId)
        val scores = assessmentSessionDao.getScoresForSession(sessionId)
        emit(session.toSnapshot(responses = responses, scores = scores))
    }

    override fun updateProgress(
        sessionId: Long,
        pageIndex: Int,
        questionIndex: Int
    ): Flow<AssessmentSessionSnapshot> = flow {
        assessmentSessionDao.updateProgress(
            sessionId = sessionId,
            pageIndex = pageIndex,
            questionIndex = questionIndex
        )
        val session = assessmentSessionDao.getSessionById(sessionId)
            ?: error("Expected session after progress update")
        val responses = assessmentSessionDao.getResponsesForSession(sessionId)
        val scores = assessmentSessionDao.getScoresForSession(sessionId)
        emit(session.toSnapshot(responses = responses, scores = scores))
    }
}

private fun AssessmentSessionTable.toSnapshot(
    responses: List<ResponseTable>,
    scores: List<SephiraScoreTable>
): AssessmentSessionSnapshot {
    return AssessmentSessionSnapshot(
        sessionId = id,
        questionnaireVersion = questionnaireVersion,
        status = status,
        currentSephiraId = currentSephiraId,
        currentPageIndex = currentPageIndex,
        currentQuestionIndex = currentQuestionIndex,
        totalQuestions = totalQuestions,
        startedAt = startedAt,
        completedAt = completedAt,
        responses = responses.map { response ->
            SavedResponse(
                questionId = response.questionId,
                selectedOptionId = response.selectedOptionId,
                numericValue = response.numericValue,
                questionOrder = response.questionOrder,
                answeredAt = response.answeredAt
            )
        },
        scores = scores.map { score ->
            SephiraScore(
                sessionId = score.sessionId,
                sephiraId = score.sephiraId,
                balanceScore = score.balanceScore,
                deficiencyScore = score.deficiencyScore,
                excessScore = score.excessScore,
                dominantPole = score.dominantPole,
                confidence = score.confidence,
                isLowConfidence = score.isLowConfidence
            )
        }
    )
}

private fun SephiraScore.toTable(): SephiraScoreTable {
    return SephiraScoreTable(
        sessionId = sessionId,
        sephiraId = sephiraId,
        balanceScore = balanceScore,
        deficiencyScore = deficiencyScore,
        excessScore = excessScore,
        dominantPole = dominantPole,
        confidence = confidence,
        isLowConfidence = isLowConfidence
    )
}
