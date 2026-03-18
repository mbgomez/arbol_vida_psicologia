package com.netah.hakkam.numyah.mind.feature.results

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.feature.common.PlaceholderScreen

@Composable
fun ResultsPlaceholderScreen(
    paddingValues: PaddingValues,
    onBackHome: () -> Unit
) {
    PlaceholderScreen(
        paddingValues = paddingValues,
        title = stringResource(R.string.placeholder_results_title),
        body = stringResource(R.string.placeholder_results_body),
        actionLabel = stringResource(R.string.placeholder_primary_action),
        onAction = onBackHome
    )
}
