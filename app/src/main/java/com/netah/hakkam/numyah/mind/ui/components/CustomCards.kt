package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.netah.hakkam.numyah.mind.R


@Composable
fun CardView(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val smallPadding = dimensionResource(R.dimen.padding_small)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick?.invoke()
            },
    )
    {
        Surface(
            modifier = Modifier
                .padding(all = smallPadding),
            content = content
        )
    }
}