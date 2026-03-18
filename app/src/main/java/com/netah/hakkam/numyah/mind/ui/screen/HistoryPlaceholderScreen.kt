package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R

@Composable
fun HistoryPlaceholderScreen(
    paddingValues: PaddingValues
) {
    PlaceholderScreen(
        paddingValues = paddingValues,
        title = stringResource(R.string.placeholder_history_title),
        body = stringResource(R.string.placeholder_history_body)
    )
}
