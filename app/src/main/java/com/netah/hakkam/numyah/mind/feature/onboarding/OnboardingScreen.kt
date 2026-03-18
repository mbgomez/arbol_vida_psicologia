package com.netah.hakkam.numyah.mind.feature.onboarding

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R

private data class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int
)

@Composable
fun OnboardingRoute(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    OnboardingScreen(
        uiState = uiState,
        onBack = viewModel::onBack,
        onContinue = { viewModel.onContinue(onFinish) },
        onSkip = { viewModel.skip(onFinish) }
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(R.string.onboarding_page_1_title, R.string.onboarding_page_1_body),
        OnboardingPage(R.string.onboarding_page_2_title, R.string.onboarding_page_2_body),
        OnboardingPage(R.string.onboarding_page_3_title, R.string.onboarding_page_3_body)
    )
    val page = pages[uiState.currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .align(Alignment.TopCenter)
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(
                            R.string.onboarding_step_counter,
                            uiState.currentPage + 1,
                            uiState.pageCount
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(page.titleRes),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(page.bodyRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(uiState.pageCount) { index ->
                            Box(
                                modifier = Modifier
                                    .size(
                                        width = if (index == uiState.currentPage) 32.dp else 10.dp,
                                        height = 10.dp
                                    )
                                    .background(
                                        color = if (index == uiState.currentPage) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = RoundedCornerShape(999.dp)
                                    )
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = stringResource(R.string.onboarding_footer),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.isFirstPage) {
                            OutlinedButton(
                                onClick = onSkip,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = stringResource(R.string.onboarding_skip))
                            }
                        } else {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = stringResource(R.string.onboarding_back))
                            }
                        }

                        Button(
                            onClick = onContinue,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(
                                    if (uiState.isLastPage) {
                                        R.string.onboarding_finish
                                    } else {
                                        R.string.onboarding_continue
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
