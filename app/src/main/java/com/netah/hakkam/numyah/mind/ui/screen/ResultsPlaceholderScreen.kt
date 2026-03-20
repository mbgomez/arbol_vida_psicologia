package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.viewmodel.ResultsOverviewUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsSephiraUiModel
import com.netah.hakkam.numyah.mind.viewmodel.ResultsUiState
import com.netah.hakkam.numyah.mind.viewmodel.ResultsViewModel
import kotlin.math.roundToInt

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
        Text(
            text = stringResource(R.string.results_overview_title),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = stringResource(R.string.results_overview_subtitle, model.completedCount, model.totalCount),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        model.sephirot.forEach { sephira ->
            ResultsSephiraCard(model = sephira)
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
private fun ResultsSephiraCard(model: ResultsSephiraUiModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.onboarding_spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))
        ) {
            Text(
                text = model.sephiraName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = dominantLabel(model),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = confidenceLabel(model.confidence),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(
                    R.string.results_score_summary,
                    scorePercentText(model.balanceScore),
                    scorePercentText(model.deficiencyScore),
                    scorePercentText(model.excessScore)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

private fun scorePercentText(value: Double): String = "${(value * 100).roundToInt()}%"
