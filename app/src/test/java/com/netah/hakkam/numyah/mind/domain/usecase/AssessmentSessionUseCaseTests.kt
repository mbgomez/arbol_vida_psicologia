package com.netah.hakkam.numyah.mind.domain.usecase

import com.netah.hakkam.numyah.mind.data.repository.AssessmentSessionRepository
import com.netah.hakkam.numyah.mind.domain.model.AssessmentSessionSnapshot
import com.netah.hakkam.numyah.mind.domain.model.AssessmentStatus
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentSessionUseCaseTests {

    private lateinit var assessmentSessionRepository: AssessmentSessionRepository
    private lateinit var startOrResumeAssessmentUseCase: StartOrResumeAssessmentUseCase
    private lateinit var observeActiveAssessmentUseCase: ObserveActiveAssessmentUseCase
    private lateinit var observeLatestCompletedAssessmentUseCase: ObserveLatestCompletedAssessmentUseCase
    private lateinit var observeAssessmentHistoryUseCase: ObserveAssessmentHistoryUseCase
    private lateinit var observeCompletedAssessmentByIdUseCase: ObserveCompletedAssessmentByIdUseCase
    private lateinit var saveAssessmentAnswerUseCase: SaveAssessmentAnswerUseCase
    private lateinit var updateAssessmentProgressUseCase: UpdateAssessmentProgressUseCase
    private lateinit var saveAssessmentScoreUseCase: SaveAssessmentScoreUseCase
    private lateinit var advanceAssessmentSectionUseCase: AdvanceAssessmentSectionUseCase
    private lateinit var completeAssessmentUseCase: CompleteAssessmentUseCase

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        assessmentSessionRepository = mockk(relaxed = true)
        startOrResumeAssessmentUseCase = StartOrResumeAssessmentUseCase(assessmentSessionRepository)
        observeActiveAssessmentUseCase = ObserveActiveAssessmentUseCase(assessmentSessionRepository)
        observeLatestCompletedAssessmentUseCase = ObserveLatestCompletedAssessmentUseCase(assessmentSessionRepository)
        observeAssessmentHistoryUseCase = ObserveAssessmentHistoryUseCase(assessmentSessionRepository)
        observeCompletedAssessmentByIdUseCase = ObserveCompletedAssessmentByIdUseCase(assessmentSessionRepository)
        saveAssessmentAnswerUseCase = SaveAssessmentAnswerUseCase(assessmentSessionRepository)
        updateAssessmentProgressUseCase = UpdateAssessmentProgressUseCase(assessmentSessionRepository)
        saveAssessmentScoreUseCase = SaveAssessmentScoreUseCase(assessmentSessionRepository)
        advanceAssessmentSectionUseCase = AdvanceAssessmentSectionUseCase(assessmentSessionRepository)
        completeAssessmentUseCase = CompleteAssessmentUseCase(assessmentSessionRepository)
    }

    @Test
    fun startOrResumeAssessmentUseCase_delegatesParamsToRepository() = coroutinesRule.runBlockingTest {
        val expected = testSnapshot()
        val params = StartOrResumeAssessmentParams(
            questionnaireVersion = "malkuth-v1",
            initialSephiraId = SephiraId.MALKUTH,
            totalQuestions = 6,
            forceStartFresh = true
        )
        every {
            assessmentSessionRepository.startOrResumeSession(
                questionnaireVersion = "malkuth-v1",
                initialSephiraId = SephiraId.MALKUTH,
                totalQuestions = 6,
                forceStartFresh = true
            )
        } returns flowOf(expected)

        val result = startOrResumeAssessmentUseCase.run(params).toList()

        verify(exactly = 1) {
            assessmentSessionRepository.startOrResumeSession(
                questionnaireVersion = "malkuth-v1",
                initialSephiraId = SephiraId.MALKUTH,
                totalQuestions = 6,
                forceStartFresh = true
            )
        }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun observeActiveAssessmentUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = testSnapshot()
        every { assessmentSessionRepository.observeActiveSession() } returns flowOf(expected)

        val result = observeActiveAssessmentUseCase.run().toList()

        verify(exactly = 1) { assessmentSessionRepository.observeActiveSession() }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun observeLatestCompletedAssessmentUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = testSnapshot(status = AssessmentStatus.COMPLETED, completedAt = 2000L)
        every { assessmentSessionRepository.observeLatestCompletedSession() } returns flowOf(expected)

        val result = observeLatestCompletedAssessmentUseCase.run().toList()

        verify(exactly = 1) { assessmentSessionRepository.observeLatestCompletedSession() }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun observeAssessmentHistoryUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = listOf(
            testSnapshot(status = AssessmentStatus.COMPLETED, completedAt = 3000L),
            testSnapshot(status = AssessmentStatus.COMPLETED, completedAt = 2000L)
        )
        every { assessmentSessionRepository.observeCompletedSessions() } returns flowOf(expected)

        val result = observeAssessmentHistoryUseCase.run().toList()

        verify(exactly = 1) { assessmentSessionRepository.observeCompletedSessions() }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun observeCompletedAssessmentByIdUseCase_delegatesToRepository() = coroutinesRule.runBlockingTest {
        val expected = testSnapshot(status = AssessmentStatus.COMPLETED, completedAt = 2000L)
        every { assessmentSessionRepository.observeCompletedSession(42L) } returns flowOf(expected)

        val result = observeCompletedAssessmentByIdUseCase.run(42L).toList()

        verify(exactly = 1) { assessmentSessionRepository.observeCompletedSession(42L) }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun saveAssessmentAnswerUseCase_delegatesAllAnswerFields() = coroutinesRule.runBlockingTest {
        val expected = testSnapshot(currentQuestionIndex = 1)
        val params = SaveAnswerParams(
            sessionId = 42L,
            questionId = "malkuth_resources_excess",
            selectedOptionId = "agree",
            numericValue = 3,
            questionOrder = 0,
            nextPageIndex = 0,
            nextQuestionIndex = 1
        )
        every {
            assessmentSessionRepository.saveAnswer(
                sessionId = 42L,
                questionId = "malkuth_resources_excess",
                selectedOptionId = "agree",
                numericValue = 3,
                questionOrder = 0,
                nextPageIndex = 0,
                nextQuestionIndex = 1
            )
        } returns flowOf(expected)

        val result = saveAssessmentAnswerUseCase.run(params).toList()

        verify(exactly = 1) {
            assessmentSessionRepository.saveAnswer(
                sessionId = 42L,
                questionId = "malkuth_resources_excess",
                selectedOptionId = "agree",
                numericValue = 3,
                questionOrder = 0,
                nextPageIndex = 0,
                nextQuestionIndex = 1
            )
        }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun completeAssessmentUseCase_delegatesSessionIdAndScore() = coroutinesRule.runBlockingTest {
        val score = SephiraScore(
            sessionId = 42L,
            sephiraId = SephiraId.MALKUTH,
            balanceScore = 0.62,
            deficiencyScore = 0.20,
            excessScore = 0.18,
            dominantPole = Pole.BALANCE,
            confidence = ConfidenceLevel.HIGH,
            isLowConfidence = false
        )
        val expected = testSnapshot(
            status = AssessmentStatus.COMPLETED,
            completedAt = 2000L,
            scores = listOf(score)
        )
        every {
            assessmentSessionRepository.completeSession(
                sessionId = 42L,
                score = score
            )
        } returns flowOf(expected)

        val result = completeAssessmentUseCase.run(42L to score).toList()

        verify(exactly = 1) {
            assessmentSessionRepository.completeSession(
                sessionId = 42L,
                score = score
            )
        }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun saveAssessmentScoreUseCase_delegatesSessionIdAndScore() = coroutinesRule.runBlockingTest {
        val score = SephiraScore(
            sessionId = 42L,
            sephiraId = SephiraId.MALKUTH,
            balanceScore = 0.62,
            deficiencyScore = 0.20,
            excessScore = 0.18,
            dominantPole = Pole.BALANCE,
            confidence = ConfidenceLevel.HIGH,
            isLowConfidence = false
        )
        val expected = testSnapshot(scores = listOf(score))
        every {
            assessmentSessionRepository.saveSephiraScore(
                sessionId = 42L,
                score = score
            )
        } returns flowOf(expected)

        val result = saveAssessmentScoreUseCase.run(42L to score).toList()

        verify(exactly = 1) {
            assessmentSessionRepository.saveSephiraScore(
                sessionId = 42L,
                score = score
            )
        }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun advanceAssessmentSectionUseCase_delegatesNextSectionAndQuestionCount() = coroutinesRule.runBlockingTest {
        val expected = testSnapshot()
        val params = AdvanceAssessmentSectionParams(
            sessionId = 42L,
            sephiraId = SephiraId.YESOD,
            totalQuestions = 6
        )
        every {
            assessmentSessionRepository.advanceToSephira(
                sessionId = 42L,
                sephiraId = SephiraId.YESOD,
                totalQuestions = 6
            )
        } returns flowOf(expected)

        val result = advanceAssessmentSectionUseCase.run(params).toList()

        verify(exactly = 1) {
            assessmentSessionRepository.advanceToSephira(
                sessionId = 42L,
                sephiraId = SephiraId.YESOD,
                totalQuestions = 6
            )
        }
        assertEquals(listOf(expected), result)
    }

    @Test
    fun updateAssessmentProgressUseCase_delegatesPositionUpdate() = coroutinesRule.runBlockingTest {
        val expected = testSnapshot(currentQuestionIndex = 1)
        val params = UpdateAssessmentProgressParams(
            sessionId = 42L,
            pageIndex = 0,
            questionIndex = 1
        )
        every {
            assessmentSessionRepository.updateProgress(
                sessionId = 42L,
                pageIndex = 0,
                questionIndex = 1
            )
        } returns flowOf(expected)

        val result = updateAssessmentProgressUseCase.run(params).toList()

        verify(exactly = 1) {
            assessmentSessionRepository.updateProgress(
                sessionId = 42L,
                pageIndex = 0,
                questionIndex = 1
            )
        }
        assertEquals(listOf(expected), result)
    }

    private fun testSnapshot(
        currentQuestionIndex: Int = 0,
        status: AssessmentStatus = AssessmentStatus.IN_PROGRESS,
        completedAt: Long? = null,
        scores: List<SephiraScore> = emptyList()
    ): AssessmentSessionSnapshot {
        return AssessmentSessionSnapshot(
            sessionId = 42L,
            questionnaireVersion = "malkuth-v1",
            status = status,
            currentSephiraId = SephiraId.MALKUTH,
            currentPageIndex = 0,
            currentQuestionIndex = currentQuestionIndex,
            totalQuestions = 6,
            startedAt = 1000L,
            completedAt = completedAt,
            responses = emptyList(),
            scores = scores
        )
    }
}
