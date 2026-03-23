package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppHighlightCard
import com.netah.hakkam.numyah.mind.ui.components.AppMetricBadge
import com.netah.hakkam.numyah.mind.ui.components.AppProgressMeter
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.ReplaceInProgressAssessmentDialog
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.assessmentConfidenceLabel
import com.netah.hakkam.numyah.mind.ui.components.assessmentDominantLabel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsOverviewUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsSephiraUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsUiState
import com.netah.hakkam.numyah.mind.viewmodel.ResultsViewModel

@Composable
fun ResultsRoute(
    paddingValues: PaddingValues,
    onPrimaryAction: () -> Unit,
    onRetakeAssessment: () -> Unit,
    primaryActionLabel: String,
    onOpenSephiraDetail: (ResultsSephiraUiModel) -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ResultsScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onPrimaryAction = onPrimaryAction,
        onRetakeAssessment = onRetakeAssessment,
        primaryActionLabel = primaryActionLabel,
        onOpenSephiraDetail = onOpenSephiraDetail
    )
}

@Composable
fun ResultsScreen(
    paddingValues: PaddingValues,
    uiState: ResultsUiState,
    onPrimaryAction: () -> Unit,
    onRetakeAssessment: () -> Unit,
    primaryActionLabel: String,
    onOpenSephiraDetail: (ResultsSephiraUiModel) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (uiState) {
            ResultsUiState.Loading -> PlaceholderScreen(
                paddingValues = PaddingValues(),
                title = stringResource(R.string.screen_results),
                body = stringResource(R.string.progress_indicator_desccription)
            )

            ResultsUiState.Empty -> PlaceholderScreen(
                paddingValues = PaddingValues(),
                title = stringResource(R.string.results_empty_title),
                body = stringResource(R.string.results_empty_body),
                actionLabel = primaryActionLabel,
                onAction = onPrimaryAction
            )

            ResultsUiState.Error -> PlaceholderScreen(
                paddingValues = PaddingValues(),
                title = stringResource(R.string.results_error_title),
                body = stringResource(R.string.results_error_body),
                actionLabel = primaryActionLabel,
                onAction = onPrimaryAction
            )

            is ResultsUiState.Loaded -> ResultsLoadedState(
                model = uiState.model,
                primaryActionLabel = primaryActionLabel,
                onPrimaryAction = onPrimaryAction,
                onRetakeAssessment = onRetakeAssessment,
                onOpenSephiraDetail = onOpenSephiraDetail
            )
        }
    }
}

@Composable
private fun ResultsLoadedState(
    model: ResultsOverviewUiModel,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    onRetakeAssessment: () -> Unit,
    onOpenSephiraDetail: (ResultsSephiraUiModel) -> Unit
) {
    var showReplaceDialog by remember { mutableStateOf(false) }

    AppScreenColumn(paddingValues = PaddingValues()) {
        ResultsSummaryCard(model = model)

        if (model.sephirot.isNotEmpty()) {
            Text(
                text = stringResource(R.string.results_ranking_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.results_ranking_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        model.needsAttention?.let { sephira ->
            ResultsSpotlightCard(
                title = stringResource(R.string.results_needs_attention_title),
                body = stringResource(
                    R.string.results_needs_attention_body,
                    sephira.sephiraName,
                    sephira.imbalancePercent
                ),
                accentColor = MaterialTheme.colorScheme.tertiary,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
            )
        }

        model.mostBalanced?.let { sephira ->
            ResultsSpotlightCard(
                title = stringResource(R.string.results_most_balanced_title),
                body = stringResource(
                    R.string.results_most_balanced_body,
                    sephira.sephiraName,
                    sephira.balancePercent
                ),
                accentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            )
        }

        model.sephirot.forEachIndexed { index, sephira ->
            ResultsSephiraCard(
                rank = index + 1,
                total = model.sephirot.size,
                model = sephira,
                onOpenSephiraDetail = { onOpenSephiraDetail(sephira) }
            )
        }

        OutlinedButton(
            onClick = {
                if (model.activeAssessment != null) {
                    showReplaceDialog = true
                } else {
                    onRetakeAssessment()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.results_retake_action))
        }

        Button(
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = primaryActionLabel)
        }
    }

    if (showReplaceDialog && model.activeAssessment != null) {
        ReplaceInProgressAssessmentDialog(
            currentSephiraName = model.activeAssessment.sephiraName,
            onConfirm = {
                showReplaceDialog = false
                onRetakeAssessment()
            },
            onDismiss = { showReplaceDialog = false }
        )
    }
}

@Composable
private fun ResultsSummaryCard(model: ResultsOverviewUiModel) {
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)

    AppSurfaceCard(
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
        elevation = 0.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Text(
                text = model.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.results_overview_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    R.string.results_overview_subtitle,
                    model.completedCount,
                    model.totalCount
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    if (model.isHistoricalSession) {
                        R.string.results_overview_status_saved
                    } else {
                        R.string.results_overview_status_latest
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResultsSpotlightCard(
    title: String,
    body: String,
    accentColor: Color,
    containerColor: Color
) {
    AppHighlightCard(
        title = title,
        body = body,
        accentColor = accentColor,
        containerColor = containerColor,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ResultsSephiraCard(
    rank: Int,
    total: Int,
    model: ResultsSephiraUiModel,
    onOpenSephiraDetail: () -> Unit
) {
    val style = resultCardStyle(model)
    val cardRadius = dimensionResource(R.dimen.radius_md)
    val borderWidth = dimensionResource(R.dimen.stroke_thin)
    val contentSpacing = dimensionResource(R.dimen.screen_section_spacing)
    val headerSpacing = dimensionResource(R.dimen.spacing_sm)

    AppSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = style.borderColor,
                shape = RoundedCornerShape(cardRadius)
            ),
        onClick = onOpenSephiraDetail,
        containerColor = style.containerColor,
        elevation = 0.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(headerSpacing)) {
                    Text(
                        text = model.sephiraName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = assessmentDominantLabel(
                            dominantPole = model.dominantPole,
                            isLowConfidence = model.isLowConfidence
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                AppMetricBadge(
                    label = stringResource(R.string.results_rank_badge, rank, total),
                    value = stringResource(R.string.results_imbalance_badge, model.imbalancePercent)
                )
            }

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

            Text(
                text = stringResource(R.string.results_open_detail_action),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun resultCardStyle(model: ResultsSephiraUiModel): ResultCardStyle {
    return when {
        model.imbalancePercent >= 110 -> ResultCardStyle(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.75f),
            borderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.65f)
        )
        model.imbalancePercent >= 75 -> ResultCardStyle(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
            borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f)
        )
        else -> ResultCardStyle(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
        )
    }
}

private data class ResultCardStyle(
    val containerColor: Color,
    val borderColor: Color
)
