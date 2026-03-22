package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.netah.hakkam.numyah.mind.R

@Composable
fun AppScreenColumn(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val horizontalPadding = dimensionResource(R.dimen.screen_padding_horizontal)
    val verticalPadding = dimensionResource(R.dimen.screen_padding_vertical)
    val sectionSpacing = dimensionResource(R.dimen.screen_section_spacing)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
        content = content
    )
}

@Composable
fun AppSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    elevation: Dp = dimensionResource(R.dimen.elevation_sm),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardRadius = dimensionResource(R.dimen.radius_md)
    val contentPadding = dimensionResource(R.dimen.spacing_xl)

    val cardModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Card(
        modifier = cardModifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun AppCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    onClick: (() -> Unit)? = null
) {
    val textSpacing = dimensionResource(R.dimen.spacing_sm)

    AppSurfaceCard(
        modifier = modifier,
        onClick = onClick
    ) {
        if (eyebrow != null) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.size(textSpacing))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.size(textSpacing))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AppHeroCard(
    eyebrow: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    val cardRadius = dimensionResource(R.dimen.radius_lg)
    val horizontalPadding = dimensionResource(R.dimen.spacing_2xl)
    val verticalPadding = dimensionResource(R.dimen.spacing_xl)
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
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
fun AppHighlightCard(
    title: String,
    body: String,
    accentColor: Color,
    containerColor: Color,
    elevation: Dp = dimensionResource(R.dimen.elevation_none),
    modifier: Modifier = Modifier
) {
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)
    val markerSize = dimensionResource(R.dimen.size_dot_sm)

    AppSurfaceCard(
        modifier = modifier,
        containerColor = containerColor,
        elevation = elevation
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(contentSpacing)) {
            Row(horizontalArrangement = Arrangement.spacedBy(contentSpacing)) {
                Box(
                    modifier = Modifier
                        .size(markerSize)
                        .background(accentColor, RoundedCornerShape(percent = 50))
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AppMetricBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val badgeRadius = dimensionResource(R.dimen.radius_pill)
    val horizontalPadding = dimensionResource(R.dimen.spacing_md)
    val verticalPadding = dimensionResource(R.dimen.spacing_sm)
    val contentSpacing = dimensionResource(R.dimen.spacing_xs)

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(badgeRadius)
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AppProgressMeter(
    label: String,
    value: Int,
    color: Color,
    valueText: String = "${value.coerceIn(0, 100)}%",
    modifier: Modifier = Modifier
) {
    val contentSpacing = dimensionResource(R.dimen.spacing_xs)
    val trackHeight = dimensionResource(R.dimen.size_dot_sm)
    val trackRadius = dimensionResource(R.dimen.radius_pill)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
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
                    .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                    .height(trackHeight)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(trackRadius)
                    )
            )
        }
    }
}

@Composable
fun AppSectionCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    showMarker: Boolean = true,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val sectionSpacing = dimensionResource(R.dimen.screen_section_spacing)
    val textSpacing = dimensionResource(R.dimen.spacing_sm)
    val markerSpacing = dimensionResource(R.dimen.spacing_md)
    val markerSize = dimensionResource(R.dimen.size_dot_sm)

    AppSurfaceCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(sectionSpacing)) {
            Column(verticalArrangement = Arrangement.spacedBy(textSpacing)) {
                if (showMarker) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(markerSpacing)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(markerSize)
                                .background(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
fun AppFooterCard(
    text: String,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.spacing_lg)
    val verticalPadding = dimensionResource(R.dimen.spacing_md)
    val cardRadius = dimensionResource(R.dimen.radius_md)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AppActionCard(
    title: String,
    body: String,
    actionLabel: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val rowSpacing = dimensionResource(R.dimen.screen_section_spacing)
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)
    val actionSpacing = dimensionResource(R.dimen.spacing_xs_plus)

    AppSurfaceCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(rowSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(contentSpacing)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(actionSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    label: String,
    modifier: Modifier = Modifier
) {
    val chipRadius = dimensionResource(R.dimen.radius_pill)
    val horizontalPadding = dimensionResource(R.dimen.spacing_md)
    val verticalPadding = dimensionResource(R.dimen.spacing_sm)
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)
    val markerSize = dimensionResource(R.dimen.size_dot_md)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(chipRadius))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f))
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        Box(
            modifier = Modifier
                .size(markerSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
