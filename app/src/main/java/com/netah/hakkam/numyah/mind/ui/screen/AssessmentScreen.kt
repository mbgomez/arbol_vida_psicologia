package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentCompletedUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentErrorType
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentHonestyNoticeUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentIntroUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentQuestionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentUiState
import kotlin.math.roundToInt
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
private fun AssessmentLoadingState() {
    val loadingDescription = stringResource(R.string.progress_indicator_desccription)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = loadingDescription
            }
        )
    }
}

@Composable
private fun AssessmentHonestyNoticeState(
    model: AssessmentHonestyNoticeUiModel,
    onCheckedChange: (Boolean) -> Unit,
    onContinue: () -> Unit
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
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))
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
        ProgressHeader(model.progress)
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
private fun AssessmentHeroImage(sephiraId: SephiraId) {
    Image(
        painter = painterResource(sephiraHeroImageRes(sephiraId)),
        contentDescription = stringResource(R.string.assessment_intro_image_description),
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.onboarding_hero_height))
            .clip(RoundedCornerShape(dimensionResource(R.dimen.onboarding_hero_corner_radius))),
        contentScale = ContentScale.Crop
    )
}

private fun sephiraHeroImageRes(sephiraId: SephiraId): Int {
    return when (sephiraId) {
        SephiraId.MALKUTH -> R.mipmap.assessment_malkut
        SephiraId.YESOD -> R.mipmap.assessment_yesod
        SephiraId.HOD -> R.mipmap.assessment_hod
        SephiraId.NETZACH -> R.mipmap.assessment_netzaj
        else -> R.mipmap.assessment_malkut
    }
}

@Composable
private fun AssessmentQuestionState(
    model: AssessmentQuestionUiModel,
    onSelectAnswer: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
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
        ProgressHeader(model.progress)
        AssessmentInfoCard(
            title = model.currentPageTitle,
            body = model.currentPageDescription
        )
        Text(
            text = model.currentQuestionPrompt,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))) {
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
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_button))
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

@Composable
private fun AssessmentCompletedState(
    model: AssessmentCompletedUiModel,
    onContinue: () -> Unit,
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
            text = stringResource(R.string.assessment_result_title, model.sephiraName),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        AssessmentResultSummaryCard(model = model)
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_result_what_it_means_title),
            body = resultMeaning(model)
        )
        AssessmentInfoCard(
            title = confidenceLabel(model.confidence),
            body = buildString {
                append(resultConfidenceNote(model))
                append("\n\n")
                append(stringResource(R.string.assessment_score_balance))
                append(": ")
                append(scorePercentText(model.balanceScore))
                append("\n")
                append(stringResource(R.string.assessment_score_deficiency))
                append(": ")
                append(scorePercentText(model.deficiencyScore))
                append("\n")
                append(stringResource(R.string.assessment_score_excess))
                append(": ")
                append(scorePercentText(model.excessScore))
            }
        )
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_result_daily_life_title),
            body = resultDailyLife(model)
        )
        AssessmentInfoCard(
            title = stringResource(R.string.assessment_result_next_step_title),
            body = resultNextStep(model)
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

@Composable
private fun AssessmentResultSummaryCard(model: AssessmentCompletedUiModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.onboarding_spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))
        ) {
            Text(
                text = model.sephiraName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = resultLabel(model),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = resultSummary(model),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AssessmentErrorState(
    errorType: AssessmentErrorType,
    onRetry: () -> Unit
) {
    val horizontalPadding = dimensionResource(R.dimen.onboarding_horizontal_padding)
    val spacing = dimensionResource(R.dimen.onboarding_spacing_medium)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = spacing),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage(errorType),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(spacing))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.assessment_retry))
        }
    }
}

@Composable
private fun ProgressHeader(model: com.netah.hakkam.numyah.mind.viewmodel.AssessmentProgressUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))) {
        Text(
            text = stringResource(
                R.string.assessment_page_counter,
                model.currentPageIndex + 1,
                model.totalPages
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        if (model.currentQuestionNumber > 0) {
            Text(
                text = stringResource(
                    R.string.assessment_question_counter,
                    model.currentQuestionNumber,
                    model.totalQuestions
                ),
                style = MaterialTheme.typography.bodyMedium,
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
                    .fillMaxWidth(model.overallProgress.coerceIn(0f, 1f))
                    .height(dimensionResource(R.dimen.onboarding_progress_dot))
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_pill_radius))
                    )
            )
        }
    }
}

@Composable
private fun AssessmentInfoCard(
    title: String,
    body: String
) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AssessmentAnswerOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = dimensionResource(R.dimen.onboarding_supporting_chip_border_width),
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_action_card_corner_radius))
            )
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_action_card_corner_radius))
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(dimensionResource(R.dimen.onboarding_spacing_large)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_medium))
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.onboarding_progress_dot))
                .background(
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(percent = 50)
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun resultLabel(model: AssessmentCompletedUiModel): String {
    return when {
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_leans_balance)
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_leans_deficiency)
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.EXCESS ->
            stringResource(R.string.assessment_result_leans_excess)
        model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_balance)
        model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_deficiency)
        else -> stringResource(R.string.assessment_result_excess)
    }
}

@Composable
private fun resultSummary(model: AssessmentCompletedUiModel): String {
    return when {
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_summary_leans_balance)
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_summary_leans_deficiency)
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.EXCESS ->
            stringResource(R.string.assessment_result_summary_leans_excess)
        model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_summary_balance)
        model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_summary_deficiency)
        else -> stringResource(R.string.assessment_result_summary_excess)
    }
}

@Composable
private fun resultMeaning(model: AssessmentCompletedUiModel): String {
    return when {
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_meaning_leans_balance)
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_meaning_leans_deficiency)
        model.isLowConfidence && model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.EXCESS ->
            stringResource(R.string.assessment_result_meaning_leans_excess)
        model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_meaning_balance)
        model.dominantPole == com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_meaning_deficiency)
        else -> stringResource(R.string.assessment_result_meaning_excess)
    }
}

@Composable
private fun resultDailyLife(model: AssessmentCompletedUiModel): String {
    return when (model.dominantPole) {
        com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_daily_life_balance)
        com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_daily_life_deficiency)
        com.netah.hakkam.numyah.mind.domain.model.Pole.EXCESS ->
            stringResource(R.string.assessment_result_daily_life_excess)
    }
}

@Composable
private fun resultNextStep(model: AssessmentCompletedUiModel): String {
    return when (model.dominantPole) {
        com.netah.hakkam.numyah.mind.domain.model.Pole.BALANCE ->
            stringResource(R.string.assessment_result_next_step_balance)
        com.netah.hakkam.numyah.mind.domain.model.Pole.DEFICIENCY ->
            stringResource(R.string.assessment_result_next_step_deficiency)
        com.netah.hakkam.numyah.mind.domain.model.Pole.EXCESS ->
            stringResource(R.string.assessment_result_next_step_excess)
    }
}

@Composable
private fun resultConfidenceNote(model: AssessmentCompletedUiModel): String {
    return when (model.confidence) {
        com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel.HIGH ->
            stringResource(R.string.assessment_confidence_note_high)
        com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel.MEDIUM ->
            stringResource(R.string.assessment_confidence_note_medium)
        com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel.LOW ->
            stringResource(R.string.assessment_confidence_note_low)
    }
}

@Composable
private fun confidenceLabel(confidence: com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel): String {
    return when (confidence) {
        com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel.HIGH -> stringResource(R.string.assessment_confidence_high)
        com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel.MEDIUM -> stringResource(R.string.assessment_confidence_medium)
        com.netah.hakkam.numyah.mind.domain.model.ConfidenceLevel.LOW -> stringResource(R.string.assessment_confidence_low)
    }
}

@Composable
private fun errorMessage(errorType: AssessmentErrorType): String {
    return when (errorType) {
        AssessmentErrorType.LOAD -> stringResource(R.string.assessment_error_load)
        AssessmentErrorType.SAVE_ANSWER -> stringResource(R.string.assessment_error_save)
        AssessmentErrorType.CONTINUE -> stringResource(R.string.assessment_error_continue)
        AssessmentErrorType.GO_BACK -> stringResource(R.string.assessment_error_back)
    }
}

private fun scorePercentText(value: Double): String = "${(value * 100).roundToInt()}%"

private const val ASSESSMENT_HONESTY_CHECKBOX_TAG = "assessment_honesty_checkbox"
