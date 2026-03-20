package com.netah.hakkam.numyah.mind.domain.scoring

import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.domain.model.QuestionFormat
import com.netah.hakkam.numyah.mind.domain.model.ScoreInput
import com.netah.hakkam.numyah.mind.domain.model.SephiraScore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssessmentScoringEngine @Inject constructor() {

    fun score(input: ScoreInput, sessionId: Long): SephiraScore {
        val section = input.questionnaire.sections.first { it.sephiraId == input.sephiraId }
        val questionsById = section.questions.associateBy { it.id }

        val maxLikertValue = when (input.questionnaire.responseScale.format) {
            QuestionFormat.LIKERT_5 -> 4
            QuestionFormat.LIKERT_7 -> 6
            QuestionFormat.SINGLE_CHOICE -> input.questionnaire.responseScale.options.maxOfOrNull { it.numericValue } ?: 0
        }.toDouble()

        val poleQuestionCounts = section.questions.groupingBy { it.targetPole }.eachCount()
        val responsesByPole = input.responses.groupBy { response ->
            questionsById.getValue(response.questionId).targetPole
        }

        val balanceScore = normalizedPoleScore(Pole.BALANCE, responsesByPole, poleQuestionCounts, maxLikertValue)
        val deficiencyScore = normalizedPoleScore(Pole.DEFICIENCY, responsesByPole, poleQuestionCounts, maxLikertValue)
        val excessScore = normalizedPoleScore(Pole.EXCESS, responsesByPole, poleQuestionCounts, maxLikertValue)

        val scorePairs = listOf(
            Pole.BALANCE to balanceScore,
            Pole.DEFICIENCY to deficiencyScore,
            Pole.EXCESS to excessScore
        ).sortedByDescending { it.second }

        val dominantPole = resolveDominantPole(scorePairs)
        val isLowConfidence = !meetsClassificationRule(
            dominantPole = dominantPole,
            balanceScore = balanceScore,
            deficiencyScore = deficiencyScore,
            excessScore = excessScore
        )
        val completionRate = if (section.questions.isEmpty()) 0.0 else input.responses.size.toDouble() / section.questions.size.toDouble()
        val dominanceGap = if (scorePairs.size > 1) scorePairs[0].second - scorePairs[1].second else scorePairs[0].second
        val confidence = resolveConfidence(completionRate = completionRate, dominanceGap = dominanceGap, isLowConfidence = isLowConfidence)

        return SephiraScore(
            sessionId = sessionId,
            sephiraId = input.sephiraId,
            balanceScore = balanceScore,
            deficiencyScore = deficiencyScore,
            excessScore = excessScore,
            dominantPole = dominantPole,
            confidence = confidence,
            isLowConfidence = isLowConfidence
        )
    }

    private fun normalizedPoleScore(
        pole: Pole,
        responsesByPole: Map<Pole, List<com.netah.hakkam.numyah.mind.domain.model.SavedResponse>>,
        poleQuestionCounts: Map<Pole, Int>,
        maxLikertValue: Double
    ): Double {
        val totalForPole = responsesByPole[pole].orEmpty().sumOf { it.numericValue.toDouble() }
        val maxForPole = (poleQuestionCounts[pole] ?: 0) * maxLikertValue
        if (maxForPole <= 0.0) {
            return 0.0
        }
        return totalForPole / maxForPole
    }

    private fun resolveDominantPole(scorePairs: List<Pair<Pole, Double>>): Pole {
        return scorePairs.maxByOrNull { it.second }?.first ?: Pole.BALANCE
    }

    private fun meetsClassificationRule(
        dominantPole: Pole,
        balanceScore: Double,
        deficiencyScore: Double,
        excessScore: Double
    ): Boolean {
        return when (dominantPole) {
            Pole.BALANCE -> {
                val nextHighest = maxOf(deficiencyScore, excessScore)
                balanceScore >= 0.55 && (balanceScore - nextHighest) >= 0.10
            }
            Pole.DEFICIENCY -> {
                deficiencyScore >= 0.45 && (deficiencyScore - balanceScore) >= 0.08
            }
            Pole.EXCESS -> {
                excessScore >= 0.45 && (excessScore - balanceScore) >= 0.08
            }
        }
    }

    private fun resolveConfidence(
        completionRate: Double,
        dominanceGap: Double,
        isLowConfidence: Boolean
    ): ConfidenceLevel {
        if (completionRate < 1.0) {
            return ConfidenceLevel.LOW
        }
        if (isLowConfidence || dominanceGap < 0.10) {
            return ConfidenceLevel.LOW
        }
        if (dominanceGap >= 0.20) {
            return ConfidenceLevel.HIGH
        }
        return ConfidenceLevel.MEDIUM
    }
}
