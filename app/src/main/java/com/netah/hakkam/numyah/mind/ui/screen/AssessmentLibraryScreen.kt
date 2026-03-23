package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.netah.hakkam.numyah.mind.ui.components.AppFooterCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.ReplaceInProgressAssessmentDialog
import com.netah.hakkam.numyah.mind.ui.components.StatusChip
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryEntryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryUiState
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentLibraryViewModel

@Composable
fun AssessmentLibraryRoute(
    paddingValues: PaddingValues,
    onOpenAssessment: () -> Unit,
    onStartFreshAssessment: () -> Unit,
    viewModel: AssessmentLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AssessmentLibraryScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onOpenAssessment = onOpenAssessment,
        onStartFreshAssessment = onStartFreshAssessment
    )
}

@Composable
fun AssessmentLibraryScreen(
    paddingValues: PaddingValues,
    uiState: AssessmentLibraryUiState,
    onOpenAssessment: () -> Unit,
    onStartFreshAssessment: () -> Unit
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
            AssessmentLibraryUiState.Loading -> AppHeroCard(
                eyebrow = stringResource(R.string.assessment_library_eyebrow),
                title = stringResource(R.string.assessment_library_loading_title),
                body = stringResource(R.string.assessment_library_loading_body)
            )
            AssessmentLibraryUiState.Error -> AppHeroCard(
                eyebrow = stringResource(R.string.assessment_library_eyebrow),
                title = stringResource(R.string.assessment_library_error_title),
                body = stringResource(R.string.assessment_library_error_body)
            )
            is AssessmentLibraryUiState.Loaded -> {
                AssessmentLibraryEntryCard(
                    model = uiState.model.entry,
                    onOpenAssessment = onOpenAssessment,
                    onStartFreshAssessment = onStartFreshAssessment
                )
                AppFooterCard(text = stringResource(R.string.assessment_library_footer))
            }
        }
    }
}

@Composable
private fun AssessmentLibraryEntryCard(
    model: AssessmentLibraryEntryUiModel,
    onOpenAssessment: () -> Unit,
    onStartFreshAssessment: () -> Unit
) {
    var showReplaceDialog by remember { mutableStateOf(false) }

    AppSurfaceCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                androidx.compose.ui.res.dimensionResource(R.dimen.screen_section_spacing)
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
                onClick = onOpenAssessment,
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
                onStartFreshAssessment()
            },
            onDismiss = { showReplaceDialog = false }
        )
    }
}
