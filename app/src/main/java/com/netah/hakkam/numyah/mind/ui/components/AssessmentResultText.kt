package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole

@Composable
fun assessmentDominantLabel(
    dominantPole: Pole,
    isLowConfidence: Boolean
): String {
    return when {
        isLowConfidence && dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_leans_balance)
        isLowConfidence && dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_leans_deficiency)
        isLowConfidence && dominantPole == Pole.EXCESS -> stringResource(R.string.assessment_result_leans_excess)
        dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_balance)
        dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_deficiency)
        else -> stringResource(R.string.assessment_result_excess)
    }
}

@Composable
fun assessmentResultSummaryText(
    sephiraName: String,
    dominantPole: Pole,
    isLowConfidence: Boolean
): String {
    return when {
        isLowConfidence && dominantPole == Pole.BALANCE -> stringResource(
            R.string.assessment_result_summary_leans_balance_generic,
            sephiraName
        )
        isLowConfidence && dominantPole == Pole.DEFICIENCY -> stringResource(
            R.string.assessment_result_summary_leans_deficiency_generic,
            sephiraName
        )
        isLowConfidence && dominantPole == Pole.EXCESS -> stringResource(
            R.string.assessment_result_summary_leans_excess_generic,
            sephiraName
        )
        dominantPole == Pole.BALANCE -> stringResource(
            R.string.assessment_result_summary_balance_generic,
            sephiraName
        )
        dominantPole == Pole.DEFICIENCY -> stringResource(
            R.string.assessment_result_summary_deficiency_generic,
            sephiraName
        )
        else -> stringResource(
            R.string.assessment_result_summary_excess_generic,
            sephiraName
        )
    }
}

fun assessmentResultMeaningText(shortMeaning: String): String = shortMeaning

fun assessmentDailyLifeText(
    dominantPole: Pole,
    healthyExpression: String,
    deficiencyPattern: String,
    excessPattern: String
): String {
    return when (dominantPole) {
        Pole.BALANCE -> healthyExpression
        Pole.DEFICIENCY -> deficiencyPattern
        Pole.EXCESS -> excessPattern
    }
}

@Composable
fun assessmentNextStepText(suggestedPractices: List<String>): String {
    return suggestedPractices.firstOrNull()
        ?: stringResource(R.string.assessment_result_next_step_fallback)
}

@Composable
fun assessmentConfidenceNoteText(confidence: ConfidenceLevel): String {
    return when (confidence) {
        ConfidenceLevel.HIGH -> stringResource(R.string.assessment_confidence_note_high)
        ConfidenceLevel.MEDIUM -> stringResource(R.string.assessment_confidence_note_medium)
        ConfidenceLevel.LOW -> stringResource(R.string.assessment_confidence_note_low)
    }
}

@Composable
fun assessmentConfidenceLabel(confidence: ConfidenceLevel): String {
    return when (confidence) {
        ConfidenceLevel.HIGH -> stringResource(R.string.assessment_confidence_high)
        ConfidenceLevel.MEDIUM -> stringResource(R.string.assessment_confidence_medium)
        ConfidenceLevel.LOW -> stringResource(R.string.assessment_confidence_low)
    }
}
