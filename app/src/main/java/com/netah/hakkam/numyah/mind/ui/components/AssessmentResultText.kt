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
    dominantPole: Pole,
    isLowConfidence: Boolean
): String {
    return when {
        isLowConfidence && dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_summary_leans_balance)
        isLowConfidence && dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_summary_leans_deficiency)
        isLowConfidence && dominantPole == Pole.EXCESS -> stringResource(R.string.assessment_result_summary_leans_excess)
        dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_summary_balance)
        dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_summary_deficiency)
        else -> stringResource(R.string.assessment_result_summary_excess)
    }
}

@Composable
fun assessmentResultMeaningText(
    dominantPole: Pole,
    isLowConfidence: Boolean
): String {
    return when {
        isLowConfidence && dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_meaning_leans_balance)
        isLowConfidence && dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_meaning_leans_deficiency)
        isLowConfidence && dominantPole == Pole.EXCESS -> stringResource(R.string.assessment_result_meaning_leans_excess)
        dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_meaning_balance)
        dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_meaning_deficiency)
        else -> stringResource(R.string.assessment_result_meaning_excess)
    }
}

@Composable
fun assessmentDailyLifeText(dominantPole: Pole): String {
    return when (dominantPole) {
        Pole.BALANCE -> stringResource(R.string.assessment_result_daily_life_balance)
        Pole.DEFICIENCY -> stringResource(R.string.assessment_result_daily_life_deficiency)
        Pole.EXCESS -> stringResource(R.string.assessment_result_daily_life_excess)
    }
}

@Composable
fun assessmentNextStepText(dominantPole: Pole): String {
    return when (dominantPole) {
        Pole.BALANCE -> stringResource(R.string.assessment_result_next_step_balance)
        Pole.DEFICIENCY -> stringResource(R.string.assessment_result_next_step_deficiency)
        Pole.EXCESS -> stringResource(R.string.assessment_result_next_step_excess)
    }
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
