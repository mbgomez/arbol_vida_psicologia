package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppMetricBadge
import com.netah.hakkam.numyah.mind.ui.components.AppProgressMeter
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentInfoCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentResultSummaryCard
import com.netah.hakkam.numyah.mind.ui.components.StatusChip
import com.netah.hakkam.numyah.mind.ui.components.assessmentConfidenceLabel
import com.netah.hakkam.numyah.mind.ui.components.assessmentDominantLabel
import com.netah.hakkam.numyah.mind.viewmodel.SephiraDetailUiModel
import com.netah.hakkam.numyah.mind.viewmodel.SephiraDetailUiState
import com.netah.hakkam.numyah.mind.viewmodel.SephiraDetailViewModel

internal const val SEPHIRA_DETAIL_SCROLL_TAG = "sephira_detail_scroll"

@Composable
fun SephiraDetailRoute(
    paddingValues: PaddingValues,
    viewModel: SephiraDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SephiraDetailScreen(
        paddingValues = paddingValues,
        uiState = uiState
    )
}

@Composable
fun SephiraDetailScreen(
    paddingValues: PaddingValues,
    uiState: SephiraDetailUiState
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (uiState) {
            SephiraDetailUiState.Loading -> PlaceholderScreen(
                paddingValues = PaddingValues(),
                title = stringResource(R.string.sephira_detail_loading_title),
                body = stringResource(R.string.sephira_detail_loading_body)
            )
            SephiraDetailUiState.NotFound -> PlaceholderScreen(
                paddingValues = PaddingValues(),
                title = stringResource(R.string.sephira_detail_not_found_title),
                body = stringResource(R.string.sephira_detail_not_found_body)
            )
            SephiraDetailUiState.Error -> PlaceholderScreen(
                paddingValues = PaddingValues(),
                title = stringResource(R.string.sephira_detail_error_title),
                body = stringResource(R.string.sephira_detail_error_body)
            )
            is SephiraDetailUiState.Loaded -> SephiraDetailLoadedState(
                paddingValues = PaddingValues(),
                model = uiState.model
            )
        }
    }
}

@Composable
private fun SephiraDetailLoadedState(
    paddingValues: PaddingValues,
    model: SephiraDetailUiModel
) {
    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag(SEPHIRA_DETAIL_SCROLL_TAG)
    ) {
        Text(
            text = model.sephiraName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = model.shortMeaning,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        StatusChip(
            label = stringResource(
                if (model.isHistoricalSession) {
                    R.string.results_overview_status_saved
                } else {
                    R.string.results_overview_status_latest
                }
            )
        )

        AssessmentResultSummaryCard(
            eyebrow = model.sephiraName,
            title = assessmentDominantLabel(
                dominantPole = model.dominantPole,
                isLowConfidence = model.isLowConfidence
            ),
            body = stringResource(
                R.string.sephira_detail_summary,
                model.sephiraName,
                assessmentDominantLabel(
                    dominantPole = model.dominantPole,
                    isLowConfidence = model.isLowConfidence
                ).lowercase()
            )
        )

        AppSurfaceCard {
            Text(
                text = stringResource(R.string.sephira_detail_score_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = assessmentConfidenceLabel(model.confidence),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AppProgressMeter(
                label = stringResource(R.string.assessment_score_balance),
                value = model.balancePercent,
                color = MaterialTheme.colorScheme.primary,
                valueText = stringResource(R.string.results_percent_value, model.balancePercent)
            )
            AppProgressMeter(
                label = stringResource(R.string.assessment_score_deficiency),
                value = model.deficiencyPercent,
                color = MaterialTheme.colorScheme.secondary,
                valueText = stringResource(R.string.results_percent_value, model.deficiencyPercent)
            )
            AppProgressMeter(
                label = stringResource(R.string.assessment_score_excess),
                value = model.excessPercent,
                color = MaterialTheme.colorScheme.tertiary,
                valueText = stringResource(R.string.results_percent_value, model.excessPercent)
            )
        }

        AssessmentInfoCard(
            title = stringResource(R.string.sephira_detail_healthy_expression_title),
            body = model.healthyExpression
        )
        AssessmentInfoCard(
            title = stringResource(R.string.sephira_detail_deficiency_pattern_title),
            body = model.deficiencyPattern
        )
        AssessmentInfoCard(
            title = stringResource(R.string.sephira_detail_excess_pattern_title),
            body = model.excessPattern
        )

        AppSurfaceCard {
            Text(
                text = stringResource(R.string.sephira_detail_practices_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            model.suggestedPractices.forEachIndexed { index, practice ->
                Text(
                    text = stringResource(R.string.sephira_detail_practice_item, index + 1, practice),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AppMetricBadge(
            label = stringResource(R.string.sephira_detail_current_tendency_label),
            value = assessmentDominantLabel(
                dominantPole = model.dominantPole,
                isLowConfidence = model.isLowConfidence
            )
        )
    }
}
