package com.netah.hakkam.numyah.mind.data.repository

import com.netah.hakkam.numyah.mind.data.local.content.JsonAssessmentContentDataSource
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
import kotlin.math.absoluteValue

interface AssessmentSessionRepository {
    fun startOrResumeSession(
        questionnaireVersion: String,
        initialSephiraId: SephiraId,
        totalQuestions: Int,
        forceStartFresh: Boolean
    ): Flow<AssessmentSessionSnapshot>

    fun observeActiveSession(): Flow<AssessmentSessionSnapshot?>

    fun observeLatestCompletedSession(): Flow<AssessmentSessionSnapshot?>

    fun observeCompletedSessions(): Flow<List<AssessmentSessionSnapshot>>

    fun observeCompletedSession(sessionId: Long): Flow<AssessmentSessionSnapshot?>

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
    private val assessmentSessionDao: AssessmentSessionDao,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val jsonAssessmentContentDataSource: JsonAssessmentContentDataSource
) : AssessmentSessionRepository {

    override fun startOrResumeSession(
        questionnaireVersion: String,
        initialSephiraId: SephiraId,
        totalQuestions: Int,
        forceStartFresh: Boolean
    ): Flow<AssessmentSessionSnapshot> = flow {
        val existingSession = assessmentSessionDao.getActiveInProgressSession()
        val sessionId = if (existingSession != null && !forceStartFresh) {
            existingSession.id
        } else {
            existingSession?.let { session ->
                assessmentSessionDao.deleteSession(session.id)
            }
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
        return appPreferencesRepository.shouldUseMockHistory().flatMapLatest { useMockHistory ->
            if (useMockHistory) {
                flowOf(mockCompletedHistory().firstOrNull())
            } else {
                assessmentSessionDao.observeLatestCompletedSession().flatMapLatest { session ->
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
        }
    }

    override fun observeCompletedSessions(): Flow<List<AssessmentSessionSnapshot>> {
        return appPreferencesRepository.shouldUseMockHistory().flatMapLatest { useMockHistory ->
            if (useMockHistory) {
                flowOf(mockCompletedHistory())
            } else {
                assessmentSessionDao.observeCompletedSessions().flatMapLatest { sessions ->
                    if (sessions.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        combine(
                            sessions.map { session ->
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
                        ) { snapshots ->
                            snapshots.toList()
                        }
                    }
                }
            }
        }
    }

    override fun observeCompletedSession(sessionId: Long): Flow<AssessmentSessionSnapshot?> {
        return appPreferencesRepository.shouldUseMockHistory().flatMapLatest { useMockHistory ->
            if (useMockHistory) {
                flowOf(mockCompletedHistory().firstOrNull { it.sessionId == sessionId })
            } else {
                assessmentSessionDao.observeCompletedSession(sessionId).flatMapLatest { session ->
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

    private fun mockCompletedHistory(): List<AssessmentSessionSnapshot> {
        val questionnaire = jsonAssessmentContentDataSource.getCurrentQuestionnaire()
        val totalQuestions = questionnaire.sections.sumOf { it.questions.size }
        val lastSephira = questionnaire.sections.lastOrNull()?.sephiraId ?: SephiraId.MALKUTH
        val now = System.currentTimeMillis()

        return (0 until MOCK_HISTORY_COUNT).map { sessionIndex ->
            val completedAt = now - (sessionIndex.toLong() * MOCK_SESSION_INTERVAL_MILLIS)
            val startedAt = completedAt - MOCK_SESSION_DURATION_MILLIS
            val sessionId = MOCK_SESSION_ID_BASE + sessionIndex

            AssessmentSessionSnapshot(
                sessionId = sessionId,
                questionnaireVersion = questionnaire.version,
                status = AssessmentStatus.COMPLETED,
                currentSephiraId = lastSephira,
                currentPageIndex = 0,
                currentQuestionIndex = 0,
                totalQuestions = totalQuestions,
                startedAt = startedAt,
                completedAt = completedAt,
                responses = emptyList(),
                scores = questionnaire.sections.mapIndexed { sephiraIndex, section ->
                    buildMockScore(
                        sessionId = sessionId,
                        sessionIndex = sessionIndex,
                        sephiraIndex = sephiraIndex,
                        sephiraId = section.sephiraId
                    )
                }
            )
        }
    }

    private fun buildMockScore(
        sessionId: Long,
        sessionIndex: Int,
        sephiraIndex: Int,
        sephiraId: SephiraId
    ): SephiraScore {
        val baseBalance = 0.34 + (((sessionIndex * 7) + (sephiraIndex * 5)) % 32) / 100.0
        val deficiency = 0.14 + (((sessionIndex * 11) + (sephiraIndex * 3)) % 36) / 100.0
        val excess = 0.12 + (((sessionIndex * 5) + (sephiraIndex * 9)) % 34) / 100.0
        val balance = (baseBalance - (deficiency + excess - 0.48) * 0.35).coerceIn(0.18, 0.78)
        val dominantPole = dominantPole(
            balance = balance,
            deficiency = deficiency,
            excess = excess
        )
        val confidence = confidenceLevel(
            balance = balance,
            deficiency = deficiency,
            excess = excess
        )
        val highest = maxOf(balance, deficiency, excess)
        val secondHighest = listOf(balance, deficiency, excess).sortedDescending()[1]

        return SephiraScore(
            sessionId = sessionId,
            sephiraId = sephiraId,
            balanceScore = balance,
            deficiencyScore = deficiency,
            excessScore = excess,
            dominantPole = dominantPole,
            confidence = confidence,
            isLowConfidence = (highest - secondHighest).absoluteValue < 0.08
        )
    }

    private fun dominantPole(
        balance: Double,
        deficiency: Double,
        excess: Double
    ): Pole {
        return when (maxOf(balance, deficiency, excess)) {
            balance -> Pole.BALANCE
            deficiency -> Pole.DEFICIENCY
            else -> Pole.EXCESS
        }
    }

    private fun confidenceLevel(
        balance: Double,
        deficiency: Double,
        excess: Double
    ): ConfidenceLevel {
        val sorted = listOf(balance, deficiency, excess).sortedDescending()
        val gap = sorted[0] - sorted[1]
        return when {
            gap >= 0.18 -> ConfidenceLevel.HIGH
            gap >= 0.08 -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }
    }

    private companion object {
        const val MOCK_HISTORY_COUNT = 10
        const val MOCK_SESSION_ID_BASE = 9_000_000L
        const val MOCK_SESSION_INTERVAL_MILLIS = 3L * 24L * 60L * 60L * 1000L
        const val MOCK_SESSION_DURATION_MILLIS = 28L * 60L * 1000L
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
