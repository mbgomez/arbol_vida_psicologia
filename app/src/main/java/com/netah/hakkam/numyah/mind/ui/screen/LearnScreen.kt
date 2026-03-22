package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppActionCard
import com.netah.hakkam.numyah.mind.ui.components.AppFooterCard
import com.netah.hakkam.numyah.mind.ui.components.AppMetricBadge
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.StatusChip
import com.netah.hakkam.numyah.mind.viewmodel.LearnCatalogUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseViewModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionListItemUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionViewModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnViewModel

@Composable
fun LearnRoute(
    paddingValues: PaddingValues,
    onOpenCourse: (String) -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LearnScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onRetry = viewModel::retry,
        onOpenCourse = onOpenCourse
    )
}

@Composable
fun LearnCourseRoute(
    paddingValues: PaddingValues,
    onOpenSection: (String, String) -> Unit,
    viewModel: LearnCourseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LearnCourseScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onRetry = viewModel::retry,
        onOpenSection = onOpenSection
    )
}

@Composable
fun LearnSectionRoute(
    paddingValues: PaddingValues,
    onOpenSection: (String, String) -> Unit,
    viewModel: LearnSectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LearnSectionScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onRetry = viewModel::retry,
        onOpenSection = onOpenSection,
        onMarkSectionCompleted = viewModel::markSectionCompleted
    )
}

@Composable
fun LearnScreen(
    paddingValues: PaddingValues,
    uiState: LearnUiState,
    onRetry: () -> Unit,
    onOpenCourse: (String) -> Unit
) {
    when (uiState) {
        LearnUiState.Loading -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_loading_title),
            body = stringResource(R.string.learn_loading_body)
        )
        LearnUiState.Error -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_error_title),
            body = stringResource(R.string.learn_error_body),
            actionLabel = stringResource(R.string.learn_retry),
            onAction = onRetry
        )
        is LearnUiState.Loaded -> LearnCatalogContent(
            paddingValues = paddingValues,
            model = uiState.model,
            onOpenCourse = onOpenCourse
        )
    }
}

@Composable
fun LearnCourseScreen(
    paddingValues: PaddingValues,
    uiState: LearnCourseUiState,
    onRetry: () -> Unit,
    onOpenSection: (String, String) -> Unit
) {
    when (uiState) {
        LearnCourseUiState.Loading -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_loading_title),
            body = stringResource(R.string.learn_loading_body)
        )
        LearnCourseUiState.NotFound -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_not_found_title),
            body = stringResource(R.string.learn_not_found_body)
        )
        LearnCourseUiState.Error -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_error_title),
            body = stringResource(R.string.learn_error_body),
            actionLabel = stringResource(R.string.learn_retry),
            onAction = onRetry
        )
        is LearnCourseUiState.Loaded -> LearnCourseContent(
            paddingValues = paddingValues,
            model = uiState.model,
            onOpenSection = onOpenSection
        )
    }
}

@Composable
fun LearnSectionScreen(
    paddingValues: PaddingValues,
    uiState: LearnSectionUiState,
    onRetry: () -> Unit,
    onOpenSection: (String, String) -> Unit,
    onMarkSectionCompleted: () -> Unit
) {
    when (uiState) {
        LearnSectionUiState.Loading -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_loading_title),
            body = stringResource(R.string.learn_loading_body)
        )
        LearnSectionUiState.Locked -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_locked_title),
            body = stringResource(R.string.learn_locked_body)
        )
        LearnSectionUiState.NotFound -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_not_found_title),
            body = stringResource(R.string.learn_not_found_body)
        )
        LearnSectionUiState.Error -> PlaceholderScreen(
            paddingValues = paddingValues,
            title = stringResource(R.string.learn_error_title),
            body = stringResource(R.string.learn_error_body),
            actionLabel = stringResource(R.string.learn_retry),
            onAction = onRetry
        )
        is LearnSectionUiState.Loaded -> LearnSectionContent(
            paddingValues = paddingValues,
            model = uiState.model,
            onOpenSection = onOpenSection,
            onMarkSectionCompleted = onMarkSectionCompleted
        )
    }
}

@Composable
private fun LearnCatalogContent(
    paddingValues: PaddingValues,
    model: LearnCatalogUiModel,
    onOpenCourse: (String) -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        Text(
            text = stringResource(R.string.learn_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.learn_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        StatusChip(label = model.title)
        model.courses.forEach { course ->
            AppActionCard(
                title = course.title,
                body = course.description,
                actionLabel = stringResource(R.string.learn_open_course_action),
                onClick = { onOpenCourse(course.id) }
            )
        }
        AppFooterCard(text = stringResource(R.string.learn_footer))
    }
}

@Composable
private fun LearnCourseContent(
    paddingValues: PaddingValues,
    model: LearnCourseUiModel,
    onOpenSection: (String, String) -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        AppSurfaceCard(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.42f),
            elevation = 0.dp
        ) {
            Text(
                text = model.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = model.subtitle,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = model.description,
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppMetricBadge(
                label = stringResource(R.string.learn_course_sections_badge),
                value = stringResource(
                    R.string.learn_sections_progress,
                    model.availableSectionCount,
                    model.totalSectionCount
                ),
                modifier = Modifier.weight(1f)
            )
            AppMetricBadge(
                label = stringResource(R.string.learn_course_time_badge),
                value = stringResource(R.string.learn_minutes_short, model.estimatedMinutes),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = stringResource(R.string.learn_sections_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(
                R.string.learn_sections_subtitle,
                model.availableSectionCount,
                model.totalSectionCount
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        model.sections.forEach { section ->
            LearnSectionCard(
                section = section,
                courseId = model.id,
                onOpenSection = onOpenSection
            )
        }

        AppFooterCard(text = stringResource(R.string.learn_course_footer))
    }
}

@Composable
private fun LearnSectionCard(
    section: LearnSectionListItemUiModel,
    courseId: String,
    onOpenSection: (String, String) -> Unit
) {
    AppSurfaceCard(
        onClick = if (section.isLocked) null else ({ onOpenSection(courseId, section.id) }),
        elevation = 0.dp,
        containerColor = when {
            section.isCompleted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.26f)
            section.isLocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
            else -> MaterialTheme.colorScheme.surface
        }
    ) {
        Text(
            text = stringResource(
                R.string.learn_section_title_with_number,
                section.order,
                section.title
            ),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = section.summary,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = when {
                section.isCompleted -> stringResource(R.string.learn_section_status_completed)
                section.isLocked -> stringResource(R.string.learn_section_status_locked)
                else -> stringResource(R.string.learn_section_status_available)
            },
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun LearnSectionContent(
    paddingValues: PaddingValues,
    model: LearnSectionUiModel,
    onOpenSection: (String, String) -> Unit,
    onMarkSectionCompleted: () -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        AppSurfaceCard(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f),
            elevation = 0.dp
        ) {
            Text(
                text = stringResource(
                    R.string.learn_section_position,
                    model.order,
                    model.totalAvailableSections
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = model.sectionTitle,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = model.summary,
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.learn_minutes_short, model.readingTimeMinutes),
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (model.isCompleted) {
                    stringResource(R.string.learn_section_status_completed)
                } else {
                    stringResource(R.string.learn_section_status_in_progress)
                },
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        model.paragraphs.forEach { paragraph ->
            AppSurfaceCard(elevation = 0.dp) {
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (!model.isCompleted) {
            Button(
                onClick = onMarkSectionCompleted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.learn_mark_section_completed))
            }
        }

        if (model.isCompleted && model.nextSectionId != null && model.nextSectionTitle != null && !model.isNextSectionLocked) {
            Button(
                onClick = { onOpenSection(model.courseId, model.nextSectionId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.learn_next_section_action,
                        model.nextSectionTitle
                    )
                )
            }
        }

        if (model.isCompleted && model.nextSectionId != null && model.nextSectionTitle != null && model.isNextSectionLocked) {
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.learn_next_section_locked,
                        model.nextSectionTitle
                    )
                )
            }
        }
    }
}
