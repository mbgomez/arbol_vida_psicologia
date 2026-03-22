package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import com.netah.hakkam.numyah.mind.viewmodel.AssessmentProgressUiModel

@Composable
fun AssessmentScreenColumn(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = modifier,
        content = content
    )
}

@Composable
fun AssessmentHeroImage(
    sephiraId: SephiraId,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(sephiraHeroImageRes(sephiraId)),
        contentDescription = stringResource(R.string.assessment_intro_image_description),
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.onboarding_hero_height))
            .clip(RoundedCornerShape(dimensionResource(R.dimen.onboarding_hero_corner_radius))),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun AssessmentProgressHeader(model: AssessmentProgressUiModel) {
    val contentSpacing = dimensionResource(R.dimen.spacing_xs)
    val trackHeight = dimensionResource(R.dimen.size_dot_sm)
    val trackRadius = dimensionResource(R.dimen.radius_pill)

    Column(verticalArrangement = Arrangement.spacedBy(contentSpacing)) {
        Text(
            text = stringResource(
                R.string.assessment_page_counter,
                model.currentPageIndex + 1,
                model.totalPages
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        if (model.currentQuestionNumber > 0) {
            Text(
                text = stringResource(
                    R.string.assessment_question_counter,
                    model.currentQuestionNumber,
                    model.totalQuestions
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(trackRadius)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(model.overallProgress.coerceIn(0f, 1f))
                    .height(trackHeight)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(trackRadius)
                    )
            )
        }
    }
}

@Composable
fun AssessmentInfoCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)

    AppSurfaceCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(contentSpacing)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AssessmentResultSummaryCard(
    eyebrow: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)

    AppSurfaceCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        elevation = dimensionResource(R.dimen.elevation_none)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(contentSpacing)) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AssessmentAnswerOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderWidth = dimensionResource(R.dimen.stroke_thin)
    val optionRadius = dimensionResource(R.dimen.radius_md)
    val contentPadding = dimensionResource(R.dimen.spacing_xl)
    val contentSpacing = dimensionResource(R.dimen.spacing_md)
    val indicatorSize = dimensionResource(R.dimen.size_dot_sm)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                },
                shape = RoundedCornerShape(optionRadius)
            )
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = RoundedCornerShape(optionRadius)
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .background(
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(percent = 50)
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun sephiraHeroImageRes(sephiraId: SephiraId): Int {
    return when (sephiraId) {
        SephiraId.MALKUTH -> R.mipmap.assessment_malkut
        SephiraId.YESOD -> R.mipmap.assessment_yesod
        SephiraId.HOD -> R.mipmap.assessment_hod
        SephiraId.NETZACH -> R.mipmap.assessment_netzaj
        else -> R.mipmap.assessment_malkut
    }
}
