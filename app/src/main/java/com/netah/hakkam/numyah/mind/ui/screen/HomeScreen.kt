package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.ui.components.AppCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSectionCard
import com.netah.hakkam.numyah.mind.viewmodel.HomeSummaryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HomeUiState
import com.netah.hakkam.numyah.mind.viewmodel.HomeViewModel

@Composable
fun HomeRoute(
    paddingValues: PaddingValues,
    onStartAssessment: () -> Unit,
    onOpenResults: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onStartAssessment = onStartAssessment,
        onOpenResults = onOpenResults,
        onOpenHistory = onOpenHistory,
        onOpenLearn = onOpenLearn,
        onOpenSettings = onOpenSettings
    )
}

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    onStartAssessment: () -> Unit,
    onOpenResults: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onStartAssessment,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.home_primary_cta))
        }
        OutlinedButton(
            onClick = onOpenResults,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.home_secondary_cta))
        }

        when (uiState) {
            HomeUiState.Loading -> AppHeroCard(
                eyebrow = stringResource(R.string.home_summary_eyebrow),
                title = stringResource(R.string.home_summary_loading_title),
                body = stringResource(R.string.home_summary_loading_body)
            )
            HomeUiState.Empty -> AppHeroCard(
                eyebrow = stringResource(R.string.home_summary_eyebrow),
                title = stringResource(R.string.home_summary_empty_title),
                body = stringResource(R.string.home_summary_empty_body)
            )
            HomeUiState.Error -> AppHeroCard(
                eyebrow = stringResource(R.string.home_summary_eyebrow),
                title = stringResource(R.string.home_summary_error_title),
                body = stringResource(R.string.home_summary_error_body)
            )
            is HomeUiState.Loaded -> HomeSummarySection(model = uiState.model)
        }

        AppCard(
            title = stringResource(R.string.home_card_history_title),
            body = stringResource(R.string.home_card_history_body),
            onClick = onOpenHistory
        )
        AppCard(
            title = stringResource(R.string.home_card_learn_title),
            body = stringResource(R.string.home_card_learn_body),
            onClick = onOpenLearn
        )
        AppCard(
            title = stringResource(R.string.home_card_settings_title),
            body = stringResource(R.string.home_card_settings_body),
            onClick = onOpenSettings
        )
    }
}

@Composable
private fun HomeSummarySection(model: HomeSummaryUiModel) {
    AppSectionCard(
        title = stringResource(R.string.home_summary_title),
        body = stringResource(
            R.string.home_summary_body,
            model.lastAssessmentDate,
            daysSinceText(model.daysSinceLastAssessment)
        ),
        showMarker = false
    ) {
        AppCard(
            title = stringResource(
                R.string.home_summary_tension_title,
                model.needsAttentionSephiraName
            ),
            body = stringResource(
                R.string.home_summary_balance_title,
                model.mostBalancedSephiraName
            ),
            eyebrow = stringResource(R.string.home_summary_snapshot_eyebrow)
        )
        AppCard(
            title = stringResource(R.string.home_summary_focus_title),
            body = currentFocusText(model),
            eyebrow = stringResource(R.string.home_summary_focus_eyebrow)
        )
    }
}

@Composable
private fun daysSinceText(daysSinceLastAssessment: Int): String {
    return when (daysSinceLastAssessment) {
        0 -> stringResource(R.string.home_days_since_today)
        1 -> stringResource(R.string.home_days_since_yesterday)
        else -> stringResource(R.string.home_days_since_days, daysSinceLastAssessment)
    }
}

@Composable
private fun currentFocusText(model: HomeSummaryUiModel): String {
    return when (model.currentFocus.dominantPole) {
        Pole.BALANCE -> stringResource(
            R.string.home_focus_balance,
            model.currentFocus.sephiraName
        )
        Pole.DEFICIENCY -> stringResource(
            R.string.home_focus_deficiency,
            model.currentFocus.sephiraName
        )
        Pole.EXCESS -> stringResource(
            R.string.home_focus_excess,
            model.currentFocus.sephiraName
        )
    }
}
