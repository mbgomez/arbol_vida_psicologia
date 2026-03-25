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
