package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppFooterCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSectionCard
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.ReplaceInProgressAssessmentDialog
import com.netah.hakkam.numyah.mind.ui.components.StatusChip
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryEntryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryUiState
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryViewModel

@Composable
fun AssessmentLibraryRoute(
    paddingValues: PaddingValues,
    onOpenAssessment: (Boolean) -> Unit,
    onStartFreshAssessment: () -> Unit,
    onConfirmStartFreshAssessment: () -> Unit,
    viewModel: AssessmentLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AssessmentLibraryScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onRetry = viewModel::retry,
        onOpenAssessment = onOpenAssessment,
        onStartFreshAssessment = onStartFreshAssessment,
        onConfirmStartFreshAssessment = onConfirmStartFreshAssessment
    )
}

@Composable
fun AssessmentLibraryScreen(
    paddingValues: PaddingValues,
    uiState: AssessmentLibraryUiState,
    onRetry: () -> Unit,
    onOpenAssessment: (Boolean) -> Unit,
    onStartFreshAssessment: () -> Unit,
    onConfirmStartFreshAssessment: () -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        Text(
            text = stringResource(R.string.assessment_library_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.assessment_library_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when (uiState) {
            AssessmentLibraryUiState.Loading -> AssessmentLibraryLoadingState(
                onOpenAssessment = { onOpenAssessment(false) }
            )
            AssessmentLibraryUiState.Error -> AssessmentLibraryErrorState(
                onRetry = onRetry,
                onOpenAssessment = { onOpenAssessment(false) }
            )
            is AssessmentLibraryUiState.Loaded -> {
                AssessmentLibraryEntryCard(
                    model = uiState.model.entry,
                    onOpenAssessment = onOpenAssessment,
                    onStartFreshAssessment = onStartFreshAssessment,
                    onConfirmStartFreshAssessment = onConfirmStartFreshAssessment
                )
                AppFooterCard(text = stringResource(R.string.assessment_library_footer))
            }
        }
    }
}

@Composable
private fun AssessmentLibraryLoadingState(onOpenAssessment: () -> Unit) {
    AppHeroCard(
        eyebrow = stringResource(R.string.assessment_library_eyebrow),
        title = stringResource(R.string.assessment_library_loading_title),
        body = stringResource(R.string.assessment_library_loading_body)
    )
    AppSectionCard(
        title = stringResource(R.string.assessment_library_loading_card_title),
        body = stringResource(R.string.assessment_library_loading_card_body),
        modifier = Modifier.testTag("assessment_library_loading_card"),
        showMarker = false
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("assessment_library_loading_indicator"),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
    OutlinedButton(
        onClick = onOpenAssessment,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("assessment_library_loading_secondary_action")
    ) {
        Text(text = stringResource(R.string.assessment_library_loading_secondary_action))
    }
}

@Composable
private fun AssessmentLibraryErrorState(
    onRetry: () -> Unit,
    onOpenAssessment: () -> Unit
) {
    AppHeroCard(
        eyebrow = stringResource(R.string.assessment_library_eyebrow),
        title = stringResource(R.string.assessment_library_error_title),
        body = stringResource(R.string.assessment_library_error_body)
    )
    AppSectionCard(
        title = stringResource(R.string.assessment_library_error_card_title),
        body = stringResource(R.string.assessment_library_error_card_body),
        modifier = Modifier.testTag("assessment_library_error_card"),
        showMarker = false
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
        ) {
            AssessmentLibrarySupportLine(text = stringResource(R.string.assessment_library_error_point_one))
            AssessmentLibrarySupportLine(text = stringResource(R.string.assessment_library_error_point_two))
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
    ) {
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("assessment_library_retry_action")
        ) {
            Text(text = stringResource(R.string.assessment_library_retry_action))
        }
        OutlinedButton(
            onClick = onOpenAssessment,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("assessment_library_error_secondary_action")
        ) {
            Text(text = stringResource(R.string.assessment_library_error_secondary_action))
        }
    }
}

@Composable
private fun AssessmentLibraryEntryCard(
    model: AssessmentLibraryEntryUiModel,
    onOpenAssessment: (Boolean) -> Unit,
    onStartFreshAssessment: () -> Unit,
    onConfirmStartFreshAssessment: () -> Unit
) {
    var showReplaceDialog by remember { mutableStateOf(false) }

    AppSurfaceCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.screen_section_spacing)
            )
        ) {
            StatusChip(
                label = stringResource(
                    if (model.activeAssessment != null) {
                        R.string.assessment_library_status_in_progress
                    } else {
                        R.string.assessment_library_status_ready
                    }
                )
            )
            Text(
                text = model.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    R.string.assessment_library_tree_description,
                    model.sephiraCount
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = model.activeAssessment?.let { activeAssessment ->
                    if (activeAssessment.isAtSectionStart) {
                        stringResource(
                            R.string.assessment_library_resume_intro_body,
                            activeAssessment.sephiraName,
                            activeAssessment.completedSephirotCount,
                            activeAssessment.totalSephirotCount
                        )
                    } else {
                        stringResource(
                            R.string.assessment_library_resume_question_body,
                            activeAssessment.sephiraName,
                            activeAssessment.currentQuestionNumber,
                            activeAssessment.totalQuestions,
                            activeAssessment.completedSephirotCount,
                            activeAssessment.totalSephirotCount
                        )
                    }
                } ?: stringResource(R.string.assessment_library_start_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = { onOpenAssessment(model.activeAssessment != null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        if (model.activeAssessment != null) {
                            R.string.assessment_library_resume_action
                        } else {
                            R.string.assessment_library_start_action
                        }
                    )
                )
            }

            if (model.activeAssessment != null) {
                OutlinedButton(
                    onClick = { showReplaceDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.assessment_library_start_fresh_action))
                }
            }
        }
    }

    if (showReplaceDialog && model.activeAssessment != null) {
        ReplaceInProgressAssessmentDialog(
            currentSephiraName = model.activeAssessment.sephiraName,
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
private fun AssessmentLibrarySupportLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium
    )
}
