package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentScreenColumn
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentErrorType

@Composable
internal fun AssessmentErrorState(
    errorType: AssessmentErrorType,
    onRetry: () -> Unit
) {
    AssessmentScreenColumn {
        AppHeroCard(
            eyebrow = stringResource(R.string.assessment_error_eyebrow),
            title = errorTitle(errorType),
            body = errorMessage(errorType)
        )
        AppSurfaceCard {
            Text(
                text = errorSupportMessage(errorType),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.assessment_retry))
        }
    }
}

@Composable
private fun errorTitle(errorType: AssessmentErrorType): String {
    return when (errorType) {
        AssessmentErrorType.LOAD -> stringResource(R.string.assessment_error_load_title)
        AssessmentErrorType.SAVE_ANSWER -> stringResource(R.string.assessment_error_save_title)
        AssessmentErrorType.CONTINUE -> stringResource(R.string.assessment_error_continue_title)
        AssessmentErrorType.GO_BACK -> stringResource(R.string.assessment_error_back_title)
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

@Composable
private fun errorSupportMessage(errorType: AssessmentErrorType): String {
    return when (errorType) {
        AssessmentErrorType.LOAD -> stringResource(R.string.assessment_error_load_support)
        AssessmentErrorType.SAVE_ANSWER -> stringResource(R.string.assessment_error_save_support)
        AssessmentErrorType.CONTINUE -> stringResource(R.string.assessment_error_continue_support)
        AssessmentErrorType.GO_BACK -> stringResource(R.string.assessment_error_back_support)
    }
}
