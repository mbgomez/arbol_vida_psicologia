package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AssessmentInfoCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentResultSummaryCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentScoreSummaryCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.assessmentConfidenceLabel
import com.netah.hakkam.numyah.mind.ui.components.assessmentConfidenceNoteText
import com.netah.hakkam.numyah.mind.ui.components.assessmentDominantLabel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentCompletedUiModel
import kotlin.math.roundToInt

internal const val ASSESSMENT_COMPLETED_SCROLL_TAG = "assessment_completed_scroll"

@Composable
internal fun AssessmentCompletedState(
    model: AssessmentCompletedUiModel,
    onContinue: () -> Unit,
    onBackHome: () -> Unit
) {
    AssessmentScreenColumn(modifier = Modifier.testTag(ASSESSMENT_COMPLETED_SCROLL_TAG)) {
        Text(
            text = stringResource(R.string.assessment_result_title, model.sephiraName),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        AssessmentResultSummaryCard(
            eyebrow = stringResource(R.string.assessment_result_current_tendency_title),
            title = assessmentDominantLabel(
                dominantPole = model.dominantPole,
                isLowConfidence = model.isLowConfidence
            ),
            body = model.completionReflection
        )
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_result_reflection_context_title),
            body = model.sectionSummary
        )
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_result_what_it_means_title),
            body = model.dominantPattern
        )
        AssessmentScoreSummaryCard(
            confidenceLabel = assessmentConfidenceLabel(model.confidence),
            confidenceNote = assessmentConfidenceNoteText(model.confidence),
            balancePercent = scorePercentValue(model.balanceScore),
            deficiencyPercent = scorePercentValue(model.deficiencyScore),
            excessPercent = scorePercentValue(model.excessScore)
        )
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_result_next_step_title),
            body = model.practiceSuggestion
                ?: stringResource(R.string.assessment_result_next_step_fallback)
        )
        Button(
            onClick = {
                if (model.hasNextSephira) {
                    onContinue()
                } else {
                    onBackHome()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (model.hasNextSephira && model.nextSephiraName != null) {
                    stringResource(
                        R.string.assessment_result_continue_action,
                        model.nextSephiraName
                    )
                } else {
                    stringResource(R.string.assessment_result_home_action)
                }
            )
        }
    }
}

private fun scorePercentValue(value: Double): Int = (value * 100).roundToInt()
