package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.netah.hakkam.numyah.mind.R

@Composable
fun PreferenceSelectionRow(
    title: String,
    body: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val optionRadius = dimensionResource(R.dimen.radius_sm)
    val borderWidth = dimensionResource(R.dimen.stroke_thin)
    val horizontalPadding = dimensionResource(R.dimen.spacing_lg)
    val verticalPadding = dimensionResource(R.dimen.spacing_lg)
    val contentSpacing = dimensionResource(R.dimen.spacing_lg)
    val textSpacing = dimensionResource(R.dimen.spacing_xs)
    val indicatorSize = dimensionResource(R.dimen.size_dot_sm)
    val controlTopPadding = dimensionResource(R.dimen.spacing_xs)

    val borderColor = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(optionRadius)
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(optionRadius)
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.Top
    ) {
        RadioButton(
            modifier = Modifier
                .padding(top = controlTopPadding)
                .testTag("${testTag}_radio"),
            selected = selected,
            onClick = null
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(textSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .padding(top = controlTopPadding + 2.dp)
                .size(indicatorSize)
                .background(
                    color = if (selected) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                    },
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun PreferenceToggleRow(
    title: String,
    body: String,
    checked: Boolean,
    rowTestTag: String,
    switchTestTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    val rowRadius = dimensionResource(R.dimen.radius_sm)
    val horizontalPadding = dimensionResource(R.dimen.screen_section_spacing)
    val verticalPadding = dimensionResource(R.dimen.spacing_lg)
    val contentSpacing = dimensionResource(R.dimen.spacing_lg)
    val textSpacing = dimensionResource(R.dimen.spacing_xs)
    val controlTopPadding = dimensionResource(R.dimen.spacing_xs)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(rowTestTag)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                shape = RoundedCornerShape(rowRadius)
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(textSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            modifier = Modifier
                .padding(top = controlTopPadding)
                .testTag(switchTestTag),
            checked = checked,
            onCheckedChange = null
        )
    }
}

@Composable
fun PreferenceActionRow(
    title: String,
    body: String,
    actionLabel: String,
    buttonTestTag: String,
    onAction: () -> Unit
) {
    val rowRadius = dimensionResource(R.dimen.radius_sm)
    val horizontalPadding = dimensionResource(R.dimen.screen_section_spacing)
    val verticalPadding = dimensionResource(R.dimen.screen_section_spacing)
    val contentSpacing = dimensionResource(R.dimen.spacing_lg)
    val textSpacing = dimensionResource(R.dimen.spacing_xs)
    val actionTopPadding = dimensionResource(R.dimen.spacing_xs)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                shape = RoundedCornerShape(rowRadius)
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(textSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(
            modifier = Modifier
                .padding(top = actionTopPadding)
                .testTag(buttonTestTag),
            onClick = onAction
        ) {
            Text(text = actionLabel)
        }
    }
}
