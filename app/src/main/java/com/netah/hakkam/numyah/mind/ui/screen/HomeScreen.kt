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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.Pole
import com.netah.hakkam.numyah.mind.ui.components.AppCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSectionCard
import com.netah.hakkam.numyah.mind.ui.components.ReplaceInProgressAssessmentDialog
import com.netah.hakkam.numyah.mind.viewmodel.HomeActiveAssessmentUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HomeUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HomeSummaryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HomeUiState
import com.netah.hakkam.numyah.mind.viewmodel.HomeViewModel

@Composable
fun HomeRoute(
    paddingValues: PaddingValues,
    onStartAssessment: () -> Unit,
    onStartFreshAssessment: () -> Unit,
    onResumeAssessment: () -> Unit,
    onConfirmStartFreshAssessment: () -> Unit,
    onOpenLatestResults: () -> Unit,
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
        onStartFreshAssessment = onStartFreshAssessment,
        onResumeAssessment = onResumeAssessment,
        onConfirmStartFreshAssessment = onConfirmStartFreshAssessment,
        onOpenLatestResults = onOpenLatestResults,
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
    onStartFreshAssessment: () -> Unit,
    onResumeAssessment: () -> Unit,
    onConfirmStartFreshAssessment: () -> Unit,
    onOpenLatestResults: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val loadedModel = (uiState as? HomeUiState.Loaded)?.model
    val activeAssessment = loadedModel?.activeAssessment
    val hasLatestReflection = loadedModel?.latestReflection != null
    var showReplaceDialog by remember { mutableStateOf(false) }

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

        if (activeAssessment != null) {
            ActiveAssessmentSection(model = activeAssessment)
        }

        Button(
            onClick = if (activeAssessment != null) onResumeAssessment else onStartAssessment,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    if (activeAssessment != null) {
                        R.string.home_resume_cta
                    } else {
                        R.string.home_primary_cta
                    }
                )
            )
        }

        if (activeAssessment != null) {
            OutlinedButton(
                onClick = { showReplaceDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.home_start_fresh_cta))
            }
        }

        if (hasLatestReflection) {
            OutlinedButton(
                onClick = onOpenLatestResults,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.home_secondary_cta))
            }
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
            is HomeUiState.Loaded -> HomeLoadedState(model = uiState.model)
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

    if (showReplaceDialog && activeAssessment != null) {
        ReplaceInProgressAssessmentDialog(
            currentSephiraName = activeAssessment.sephiraName,
            onConfirm = {
                showReplaceDialog = false
                onConfirmStartFreshAssessment()
                onStartFreshAssessment()
            },
            onDismiss = { showReplaceDialog = false }
        )
    }
}

@Composable
private fun HomeLoadedState(model: HomeUiModel) {
    model.latestReflection?.let { latestReflection ->
        HomeSummarySection(model = latestReflection)
    } ?: AppHeroCard(
        eyebrow = stringResource(R.string.home_summary_eyebrow),
        title = stringResource(R.string.home_summary_empty_title),
        body = stringResource(R.string.home_summary_empty_body)
    )
}

@Composable
private fun ActiveAssessmentSection(model: HomeActiveAssessmentUiModel) {
    AppHeroCard(
        eyebrow = stringResource(R.string.home_active_eyebrow),
        title = stringResource(R.string.home_active_title),
        body = if (model.isAtSectionStart) {
            stringResource(
                R.string.home_active_intro_body,
                model.sephiraName,
                model.completedSephirotCount,
                model.totalSephirotCount
            )
        } else {
            stringResource(
                R.string.home_active_question_body,
                model.sephiraName,
                model.currentQuestionNumber,
                model.totalQuestions,
                model.completedSephirotCount,
                model.totalSephirotCount
            )
        }
    )
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
