package com.netah.hakkam.numyah.mind.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import kotlinx.coroutines.flow.distinctUntilChanged
import com.netah.hakkam.numyah.mind.viewmodel.OnboardingUiState
import com.netah.hakkam.numyah.mind.viewmodel.OnboardingViewModel
import androidx.compose.foundation.rememberScrollState

private data class OnboardingPage(
    val imageRes: Int,
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
        onSkip = { viewModel.skip(onFinish) },
        onPageChanged = viewModel::onPageChanged
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    onPageChanged: (Int) -> Unit
) {
    val pages = onboardingPages()
    val horizontalPadding = dimensionResource(R.dimen.onboarding_horizontal_padding)
    val mediumSpacing = dimensionResource(R.dimen.onboarding_spacing_medium)
    val smallSpacing = dimensionResource(R.dimen.onboarding_spacing_small)
    val headerSpacing = dimensionResource(R.dimen.onboarding_header_spacing)
    val sectionSpacing = dimensionResource(R.dimen.onboarding_section_spacing)
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { uiState.pageCount }
    )

    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { onPageChanged(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(onboardingBackgroundBrush())
    ) {
        OnboardingBackgroundGlow()

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = horizontalPadding, vertical = mediumSpacing),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    OnboardingHeader(
                        currentPage = uiState.currentPage,
                        pageCount = uiState.pageCount
                    )
                    Spacer(modifier = Modifier.height(headerSpacing))
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { pageIndex ->
                        val currentPage = pages[pageIndex]
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = smallSpacing)
                                .verticalScroll(rememberScrollState())
                        ) {
                            OnboardingHero(
                                imageRes = currentPage.imageRes,
                                title = stringResource(R.string.onboarding_primary_visual_title)
                            )
                            Spacer(modifier = Modifier.height(sectionSpacing))
                            OnboardingBody(
                                title = stringResource(currentPage.titleRes),
                                body = stringResource(currentPage.bodyRes)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(mediumSpacing))
                    OnboardingProgressIndicator(
                        currentPage = uiState.currentPage,
                        pageCount = uiState.pageCount
                    )
                }

                OnboardingActionCard(
                    isFirstPage = uiState.isFirstPage,
                    isLastPage = uiState.isLastPage,
                    onBack = onBack,
                    onSkip = onSkip,
                    onContinue = onContinue
                )
            }
        }
    }
}

@Composable
private fun onboardingBackgroundBrush(): Brush {
    return Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        )
    )
}

@Composable
private fun BoxScope.OnboardingBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.onboarding_background_glow_height))
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                        Color.Transparent
                    )
                )
            )
            .align(Alignment.TopCenter)
    )
}

@Composable
private fun OnboardingHeader(
    currentPage: Int,
    pageCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.onboarding_eyebrow),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.onboarding_header_micro_spacing)))
            Text(
                text = stringResource(
                    R.string.onboarding_step_counter,
                    currentPage + 1,
                    pageCount
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = stringResource(R.string.onboarding_supporting_caption),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .border(
                    width = dimensionResource(R.dimen.onboarding_supporting_chip_border_width),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_pill_radius))
                )
                .padding(
                    horizontal = dimensionResource(R.dimen.onboarding_spacing_button),
                    vertical = dimensionResource(R.dimen.onboarding_spacing_small)
                )
        )
    }
}

@Composable
private fun OnboardingBody(
    title: String,
    body: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.onboarding_spacing_medium)))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun OnboardingProgressIndicator(
    currentPage: Int,
    pageCount: Int
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_small))
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(
                        width = if (index == currentPage) {
                            dimensionResource(R.dimen.onboarding_progress_active_width)
                        } else {
                            dimensionResource(R.dimen.onboarding_progress_dot)
                        },
                        height = dimensionResource(R.dimen.onboarding_progress_dot)
                    )
                    .background(
                        color = if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_pill_radius))
                    )
            )
        }
    }
}

@Composable
private fun OnboardingActionCard(
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onContinue: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.onboarding_action_card_corner_radius)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        tonalElevation = dimensionResource(R.dimen.onboarding_action_card_tonal_elevation),
        shadowElevation = dimensionResource(R.dimen.onboarding_action_card_shadow_elevation)
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.onboarding_spacing_large))) {
            Text(
                text = stringResource(R.string.onboarding_footer),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.onboarding_spacing_large)))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.onboarding_spacing_button))
            ) {
                if (isFirstPage) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(text = stringResource(R.string.onboarding_skip))
                    }
                } else {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(text = stringResource(R.string.onboarding_back))
                    }
                }

                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(
                            if (isLastPage) {
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

private fun onboardingPages(): List<OnboardingPage> = listOf(
    OnboardingPage(R.mipmap.on_boarding_1, R.string.onboarding_page_1_title, R.string.onboarding_page_1_body),
    OnboardingPage(R.mipmap.on_boarding_2, R.string.onboarding_page_2_title, R.string.onboarding_page_2_body),
    OnboardingPage(R.mipmap.on_boarding_3, R.string.onboarding_page_3_title, R.string.onboarding_page_3_body),
    OnboardingPage(R.mipmap.on_boarding_4, R.string.onboarding_page_4_title, R.string.onboarding_page_4_body),
    OnboardingPage(R.mipmap.on_boarding_5, R.string.onboarding_page_5_title, R.string.onboarding_page_5_body)
)

@Composable
private fun OnboardingHero(
    imageRes: Int,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.onboarding_hero_height))
            .clip(RoundedCornerShape(dimensionResource(R.dimen.onboarding_hero_corner_radius)))
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.72f)
                        )
                    )
                )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(dimensionResource(R.dimen.onboarding_spacing_large))
        )
    }
}
