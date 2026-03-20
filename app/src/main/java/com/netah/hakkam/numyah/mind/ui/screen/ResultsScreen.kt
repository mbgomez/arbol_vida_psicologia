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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.viewmodel.ResultsOverviewUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsSephiraUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsUiState
import com.netah.hakkam.numyah.mind.viewmodel.ResultsViewModel

@Composable
fun ResultsRoute(
    paddingValues: PaddingValues,
    onBackHome: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ResultsScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onBackHome = onBackHome
    )
}

@Composable
fun ResultsScreen(
    paddingValues: PaddingValues,
    uiState: ResultsUiState,
    onBackHome: () -> Unit
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
                actionLabel = stringResource(R.string.placeholder_primary_action),
                onAction = onBackHome
            )

            ResultsUiState.Error -> PlaceholderScreen(
                paddingValues = PaddingValues(),
                title = stringResource(R.string.results_error_title),
                body = stringResource(R.string.results_error_body),
                actionLabel = stringResource(R.string.placeholder_primary_action),
                onAction = onBackHome
            )

            is ResultsUiState.Loaded -> ResultsLoadedState(
                model = uiState.model,
                onBackHome = onBackHome
            )
        }
    }
}

@Composable
private fun ResultsLoadedState(
    model: ResultsOverviewUiModel,
    onBackHome: () -> Unit
) {
    val horizontalPadding = dimensionResource(R.dimen.onboarding_horizontal_padding)
    val spacing = dimensionResource(R.dimen.onboarding_spacing_medium)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
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
                model = sephira
            )
        }

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.placeholder_primary_action))
        }
    }
}

@Composable
private fun ResultsSummaryCard(model: ResultsOverviewUiModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.onboarding_spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))
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
                text = stringResource(R.string.results_overview_status_latest),
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
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.onboarding_spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))) {
                Box(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.onboarding_progress_dot))
                        .height(dimensionResource(R.dimen.onboarding_progress_dot))
                        .background(accentColor, RoundedCornerShape(percent = 50))
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ResultsSephiraCard(
    rank: Int,
    total: Int,
    model: ResultsSephiraUiModel
) {
    val style = resultCardStyle(model)

    Card(
        colors = CardDefaults.cardColors(containerColor = style.containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = dimensionResource(R.dimen.onboarding_supporting_chip_border_width),
                color = style.borderColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_action_card_corner_radius))
            )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.onboarding_spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_medium))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))) {
                    Text(
                        text = model.sephiraName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = dominantLabel(model),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                ResultsMetricBadge(
                    label = stringResource(R.string.results_rank_badge, rank, total),
                    value = stringResource(R.string.results_imbalance_badge, model.imbalancePercent)
                )
            }

            Text(
                text = confidenceLabel(model.confidence),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ResultsMeterRow(
                label = stringResource(R.string.assessment_score_balance),
                value = model.balancePercent,
                color = MaterialTheme.colorScheme.primary
            )
            ResultsMeterRow(
                label = stringResource(R.string.assessment_score_deficiency),
                value = model.deficiencyPercent,
                color = MaterialTheme.colorScheme.secondary
            )
            ResultsMeterRow(
                label = stringResource(R.string.assessment_score_excess),
                value = model.excessPercent,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun ResultsMetricBadge(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_pill_radius))
            )
            .padding(
                horizontal = dimensionResource(R.dimen.onboarding_spacing_medium),
                vertical = dimensionResource(R.dimen.onboarding_spacing_small)
            ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ResultsMeterRow(
    label: String,
    value: Int,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.results_percent_value, value),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.onboarding_progress_dot))
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_pill_radius))
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                    .height(dimensionResource(R.dimen.onboarding_progress_dot))
                    .background(
                        color = color,
                        shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_pill_radius))
                    )
            )
        }
    }
}

@Composable
private fun dominantLabel(model: ResultsSephiraUiModel): String {
    return when {
        model.isLowConfidence && model.dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_leans_balance)
        model.isLowConfidence && model.dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_leans_deficiency)
        model.isLowConfidence && model.dominantPole == Pole.EXCESS -> stringResource(R.string.assessment_result_leans_excess)
        model.dominantPole == Pole.BALANCE -> stringResource(R.string.assessment_result_balance)
        model.dominantPole == Pole.DEFICIENCY -> stringResource(R.string.assessment_result_deficiency)
        else -> stringResource(R.string.assessment_result_excess)
    }
}

@Composable
private fun confidenceLabel(confidence: ConfidenceLevel): String {
    return when (confidence) {
        ConfidenceLevel.HIGH -> stringResource(R.string.assessment_confidence_high)
        ConfidenceLevel.MEDIUM -> stringResource(R.string.assessment_confidence_medium)
        ConfidenceLevel.LOW -> stringResource(R.string.assessment_confidence_low)
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
