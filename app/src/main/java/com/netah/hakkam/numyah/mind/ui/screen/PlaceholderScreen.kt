package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.netah.hakkam.numyah.mind.ui.components.AppCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn

@Composable
fun PlaceholderScreen(
    paddingValues: PaddingValues,
    eyebrow: String? = null,
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    AppScreenColumn(paddingValues = paddingValues) {
        if (eyebrow != null) {
            AppHeroCard(
                eyebrow = eyebrow,
                title = title,
                body = body
            )
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            AppCard(
                title = title,
                body = body
            )
        }
        if (actionLabel != null && onAction != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = actionLabel)
            }
        }
    }
}
