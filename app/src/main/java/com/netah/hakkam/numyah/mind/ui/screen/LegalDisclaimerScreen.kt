package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppFooterCard
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSectionCard
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard

@Composable
fun LegalDisclaimerRoute(
    onContinue: (skipFuture: Boolean) -> Unit
) {
    LegalDisclaimerScreen(
        paddingValues = PaddingValues(),
        onContinue = onContinue
    )
}

@Composable
fun LegalDisclaimerScreen(
    paddingValues: PaddingValues,
    onContinue: (skipFuture: Boolean) -> Unit
) {
    var skipFuture by remember { mutableStateOf(false) }
    val sectionSpacing = dimensionResource(R.dimen.screen_section_spacing)

    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag("legal_disclaimer_scroll")
    ) {
        AppHeroCard(
            eyebrow = stringResource(R.string.legal_disclaimer_eyebrow),
            title = stringResource(R.string.legal_disclaimer_title),
            body = stringResource(R.string.legal_disclaimer_body)
        )
        AppSectionCard(
            title = stringResource(R.string.legal_disclaimer_boundary_title),
            body = stringResource(R.string.legal_disclaimer_boundary_body),
            showMarker = false
        )
        AppSectionCard(
            title = stringResource(R.string.legal_disclaimer_framework_title),
            body = stringResource(R.string.legal_disclaimer_framework_body),
            showMarker = false
        )
        AppSectionCard(
            title = stringResource(R.string.legal_disclaimer_privacy_title),
            body = stringResource(R.string.legal_disclaimer_privacy_body),
            showMarker = false
        )
        AppSurfaceCard {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(sectionSpacing)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
                ) {
                    Checkbox(
                        checked = skipFuture,
                        onCheckedChange = { skipFuture = it },
                        modifier = Modifier.testTag("legal_disclaimer_skip_checkbox")
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.legal_disclaimer_skip_label),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Button(
                    onClick = { onContinue(skipFuture) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("legal_disclaimer_continue_button")
                ) {
                    Text(text = stringResource(R.string.legal_disclaimer_continue))
                }
            }
        }
        AppFooterCard(
            text = stringResource(R.string.legal_disclaimer_footer)
        )
    }
}
