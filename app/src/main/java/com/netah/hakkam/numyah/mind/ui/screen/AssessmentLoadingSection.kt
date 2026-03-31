package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.AssessmentScreenColumn

@Composable
internal fun AssessmentLoadingState() {
    val loadingDescription = stringResource(R.string.progress_indicator_desccription)
    val cardSpacing = dimensionResource(R.dimen.spacing_md)

    AssessmentScreenColumn {
        AppHeroCard(
            eyebrow = stringResource(R.string.assessment_loading_eyebrow),
            title = stringResource(R.string.assessment_loading_title),
            body = stringResource(R.string.assessment_loading_body)
        )
        AppSurfaceCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(cardSpacing)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.semantics {
                        contentDescription = loadingDescription
                    }
                )
                Text(
                    text = stringResource(R.string.assessment_loading_support),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
