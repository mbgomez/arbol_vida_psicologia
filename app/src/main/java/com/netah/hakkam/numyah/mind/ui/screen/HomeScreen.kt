package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.StatusChip

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    onStartAssessment: () -> Unit,
    onOpenResults: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val smallSpacing = dimensionResource(R.dimen.spacing_sm)

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
        StatusChip(label = stringResource(R.string.status_ready))
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
        AppCard(
            title = stringResource(R.string.home_progress_title),
            body = stringResource(R.string.home_progress_body),
            eyebrow = stringResource(R.string.home_cards_title)
        )
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
        Spacer(modifier = Modifier.height(smallSpacing))
    }
}
