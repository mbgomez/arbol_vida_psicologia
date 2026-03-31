package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AssessmentAnswerOptionRow
import com.netah.hakkam.numyah.mind.ui.components.AssessmentHeroImage
import com.netah.hakkam.numyah.mind.ui.components.AssessmentInfoCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentProgressHeader
import com.netah.hakkam.numyah.mind.ui.components.AssessmentScreenColumn
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentHonestyNoticeUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentIntroUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentQuestionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentUiState
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentViewModel

@Composable
fun AssessmentRoute(
    paddingValues: PaddingValues,
    onBackHome: () -> Unit,
    viewModel: AssessmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AssessmentScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onStart = viewModel::startAssessment,
        onContinueFromHonestyNotice = viewModel::continueFromHonestyNotice,
        onHonestyPreferenceChanged = viewModel::setDoNotShowHonestyNoticeAgain,
        onSelectAnswer = viewModel::selectAnswer,
        onContinue = viewModel::continueAssessment,
        onContinueFromCompleted = viewModel::continueFromCompletedResult,
        onBack = viewModel::goBack,
        onRetry = viewModel::retry,
        onBackHome = onBackHome
    )
}

@Composable
fun AssessmentScreen(
    paddingValues: PaddingValues,
    uiState: AssessmentUiState,
    onStart: () -> Unit,
    onContinueFromHonestyNotice: () -> Unit,
    onHonestyPreferenceChanged: (Boolean) -> Unit,
    onSelectAnswer: (String) -> Unit,
    onContinue: () -> Unit,
    onContinueFromCompleted: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onBackHome: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (uiState) {
            AssessmentUiState.Loading -> AssessmentLoadingState()
            is AssessmentUiState.HonestyNotice -> AssessmentHonestyNoticeState(
                model = uiState.model,
                onCheckedChange = onHonestyPreferenceChanged,
                onContinue = onContinueFromHonestyNotice
            )
            is AssessmentUiState.Intro -> AssessmentIntroState(
                model = uiState.model,
                onStart = onStart
            )
            is AssessmentUiState.Question -> AssessmentQuestionState(
                model = uiState.model,
                onSelectAnswer = onSelectAnswer,
                onContinue = onContinue,
                onBack = onBack
            )
            is AssessmentUiState.Completed -> AssessmentCompletedState(
                model = uiState.model,
                onContinue = onContinueFromCompleted,
                onBackHome = onBackHome
            )
            is AssessmentUiState.Error -> AssessmentErrorState(
                errorType = uiState.errorType,
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun AssessmentHonestyNoticeState(
    model: AssessmentHonestyNoticeUiModel,
    onCheckedChange: (Boolean) -> Unit,
    onContinue: () -> Unit
) {
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)

    AssessmentScreenColumn {
        Text(
            text = stringResource(R.string.assessment_honesty_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_honesty_card_title),
            body = stringResource(R.string.assessment_honesty_body)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Checkbox(
                checked = model.isDoNotShowAgainChecked,
                modifier = Modifier.testTag(ASSESSMENT_HONESTY_CHECKBOX_TAG),
                onCheckedChange = { checked -> onCheckedChange(checked) }
            )
            Text(
                text = stringResource(R.string.assessment_honesty_skip_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.assessment_honesty_continue))
        }
    }
}

@Composable
private fun AssessmentIntroState(
    model: AssessmentIntroUiModel,
    onStart: () -> Unit
) {
    AssessmentScreenColumn {
        Text(
            text = model.sephiraName,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = model.sephiraName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        AssessmentHeroImage(sephiraId = model.sephiraId)
        AssessmentProgressHeader(model.progress)
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_intro_meaning_title),
            body = model.shortMeaning
        )
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_intro_reflection_title),
            body = model.introText
        )
        if (model.isResumeSession) {
            Text(
                text = stringResource(R.string.assessment_resume),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    if (model.isResumeSession) R.string.assessment_resume else R.string.assessment_start
                )
            )
        }
    }
}

@Composable
private fun AssessmentQuestionState(
    model: AssessmentQuestionUiModel,
    onSelectAnswer: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)
    val buttonSpacing = dimensionResource(R.dimen.spacing_lg)

    AssessmentScreenColumn {
        AssessmentProgressHeader(model.progress)
        AssessmentInfoCard(
            title = model.currentPageTitle,
            body = model.currentPageDescription
        )
        Text(
            text = model.currentQuestionPrompt,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(verticalArrangement = Arrangement.spacedBy(contentSpacing)) {
            model.answerOptions.forEach { option ->
                AssessmentAnswerOptionRow(
                    label = option.label,
                    selected = option.isSelected,
                    onClick = { onSelectAnswer(option.id) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = model.navigation.canGoBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.assessment_back))
            }
            Button(
                onClick = onContinue,
                enabled = model.navigation.canContinue,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.assessment_continue))
            }
        }
    }
}

private const val ASSESSMENT_HONESTY_CHECKBOX_TAG = "assessment_honesty_checkbox"
