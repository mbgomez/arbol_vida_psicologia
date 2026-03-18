package com.netah.hakkam.numyah.mind.feature.learn

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.feature.common.PlaceholderScreen

@Composable
fun LearnPlaceholderScreen(
    paddingValues: PaddingValues
) {
    PlaceholderScreen(
        paddingValues = paddingValues,
        title = stringResource(R.string.placeholder_learn_title),
        body = stringResource(R.string.placeholder_learn_body)
    )
}
