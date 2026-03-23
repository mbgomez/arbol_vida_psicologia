package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppFooterCard
import com.netah.hakkam.numyah.mind.ui.components.AppMetricBadge
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.ui.components.StatusChip
import com.netah.hakkam.numyah.mind.viewmodel.LearnCatalogUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseCardUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnCourseViewModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionListItemUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnSectionViewModel
import com.netah.hakkam.numyah.mind.viewmodel.LearnUiState
import com.netah.hakkam.numyah.mind.viewmodel.LearnViewModel

internal const val LEARN_CATALOG_SCROLL_TAG = "learn_catalog_scroll"
internal const val LEARN_COURSE_SCROLL_TAG = "learn_course_scroll"
internal const val LEARN_SECTION_SCROLL_TAG = "learn_section_scroll"
internal const val LEARN_MARK_SECTION_COMPLETED_BUTTON_TAG = "learn_mark_section_completed_button"

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
    onOpenCourse: (String) -> Unit,
    viewModel: LearnSectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LearnSectionScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onRetry = viewModel::retry,
        onOpenSection = onOpenSection,
        onOpenCourse = onOpenCourse,
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
            eyebrow = stringResource(R.string.screen_learn),
            title = stringResource(R.string.learn_loading_title),
            body = stringResource(R.string.learn_loading_body)
        )
        LearnUiState.Error -> PlaceholderScreen(
            paddingValues = paddingValues,
            eyebrow = stringResource(R.string.screen_learn),
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
            eyebrow = stringResource(R.string.learn_course_eyebrow),
            title = stringResource(R.string.learn_loading_title),
            body = stringResource(R.string.learn_loading_body)
        )
        LearnCourseUiState.NotFound -> PlaceholderScreen(
            paddingValues = paddingValues,
            eyebrow = stringResource(R.string.learn_course_eyebrow),
            title = stringResource(R.string.learn_not_found_title),
            body = stringResource(R.string.learn_not_found_body)
        )
        LearnCourseUiState.Error -> PlaceholderScreen(
            paddingValues = paddingValues,
            eyebrow = stringResource(R.string.learn_course_eyebrow),
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
    onOpenCourse: (String) -> Unit,
    onMarkSectionCompleted: () -> Unit
) {
    when (uiState) {
        LearnSectionUiState.Loading -> PlaceholderScreen(
            paddingValues = paddingValues,
            eyebrow = stringResource(R.string.screen_learn),
            title = stringResource(R.string.learn_loading_title),
            body = stringResource(R.string.learn_loading_body)
        )
        LearnSectionUiState.Locked -> PlaceholderScreen(
            paddingValues = paddingValues,
            eyebrow = stringResource(R.string.screen_learn),
            title = stringResource(R.string.learn_locked_title),
            body = stringResource(R.string.learn_locked_body)
        )
        LearnSectionUiState.NotFound -> PlaceholderScreen(
            paddingValues = paddingValues,
            eyebrow = stringResource(R.string.screen_learn),
            title = stringResource(R.string.learn_not_found_title),
            body = stringResource(R.string.learn_not_found_body)
        )
        LearnSectionUiState.Error -> PlaceholderScreen(
            paddingValues = paddingValues,
            eyebrow = stringResource(R.string.screen_learn),
            title = stringResource(R.string.learn_error_title),
            body = stringResource(R.string.learn_error_body),
            actionLabel = stringResource(R.string.learn_retry),
            onAction = onRetry
        )
        is LearnSectionUiState.Loaded -> LearnSectionContent(
            paddingValues = paddingValues,
            model = uiState.model,
            onOpenSection = onOpenSection,
            onOpenCourse = onOpenCourse,
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
    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag(LEARN_CATALOG_SCROLL_TAG)
    ) {
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
            LearnCourseCatalogCard(course = course, onOpenCourse = onOpenCourse)
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
    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag(LEARN_COURSE_SCROLL_TAG)
    ) {
        AppSurfaceCard(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.42f),
            elevation = 0.dp
        ) {
            Text(
                text = stringResource(R.string.learn_course_eyebrow),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = model.title,
                modifier = Modifier.padding(top = 8.dp),
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
        modifier = Modifier.testTag("learn_section_card_${section.id}"),
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
        LearnSectionCardMetadata(
            section = section,
            modifier = Modifier.padding(top = 16.dp)
        )
        if (!section.isLocked) {
            Text(
                text = stringResource(R.string.learn_open_section_action),
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LearnSectionCardMetadata(
    section: LearnSectionListItemUiModel,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val shouldStack = maxWidth < 360.dp
        val statusLabel = when {
            section.isCompleted -> stringResource(R.string.learn_section_status_completed)
            section.isLocked -> stringResource(R.string.learn_section_status_locked)
            else -> stringResource(R.string.learn_section_status_available)
        }

        if (shouldStack) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppMetricBadge(
                    label = stringResource(R.string.learn_course_time_badge),
                    value = stringResource(R.string.learn_minutes_short, section.readingTimeMinutes)
                )
                StatusChip(label = statusLabel)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppMetricBadge(
                    label = stringResource(R.string.learn_course_time_badge),
                    value = stringResource(R.string.learn_minutes_short, section.readingTimeMinutes),
                    modifier = Modifier.weight(1f)
                )
                StatusChip(label = statusLabel)
            }
        }
    }
}

@Composable
private fun LearnSectionContent(
    paddingValues: PaddingValues,
    model: LearnSectionUiModel,
    onOpenSection: (String, String) -> Unit,
    onOpenCourse: (String) -> Unit,
    onMarkSectionCompleted: () -> Unit
) {
    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag(LEARN_SECTION_SCROLL_TAG)
    ) {
        LearnReadingChapterHeader(model = model)
        LearnReadingPage(model = model)
        LearnReadingActions(
            model = model,
            onOpenSection = onOpenSection,
            onOpenCourse = onOpenCourse,
            onMarkSectionCompleted = onMarkSectionCompleted
        )
    }
}

@Composable
private fun LearnReadingChapterHeader(model: LearnSectionUiModel) {
    val containerShape = RoundedCornerShape(32.dp)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = containerShape,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = model.courseTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
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
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.28f)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        )
                    )
            )
            Text(
                text = model.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppMetricBadge(
                    label = stringResource(R.string.learn_course_time_badge),
                    value = stringResource(R.string.learn_minutes_short, model.readingTimeMinutes)
                )
                AppMetricBadge(
                    label = stringResource(R.string.learn_section_status_in_progress),
                    value = if (model.isCompleted) {
                        stringResource(R.string.learn_section_status_completed)
                    } else {
                        stringResource(R.string.learn_reader_status_ready)
                    }
                )
            }
        }
    }
}

@Composable
private fun LearnReadingPage(model: LearnSectionUiModel) {
    val pageShape = RoundedCornerShape(28.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                shape = pageShape
            ),
        shape = pageShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                model.paragraphs.forEachIndexed { index, paragraph ->
                    if (index == 0) {
                        LearnLeadParagraph(paragraph = paragraph)
                    } else {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LearnLeadParagraph(paragraph: String) {
    val trimmed = paragraph.trim()
    val lead = trimmed.take(1)
    val rest = trimmed.drop(1)

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = lead,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = rest,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LearnReadingActions(
    model: LearnSectionUiModel,
    onOpenSection: (String, String) -> Unit,
    onOpenCourse: (String) -> Unit,
    onMarkSectionCompleted: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (model.previousSectionId != null && model.previousSectionTitle != null) {
            OutlinedButton(
                onClick = { onOpenSection(model.courseId, model.previousSectionId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.learn_previous_section_action,
                        model.previousSectionTitle
                    )
                )
            }
        }

        if (!model.isCompleted) {
            Button(
                onClick = onMarkSectionCompleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(LEARN_MARK_SECTION_COMPLETED_BUTTON_TAG)
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

        if (model.isCompleted && model.nextSectionId == null) {
            Button(
                onClick = { onOpenCourse(model.courseId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.learn_back_to_course_action))
            }
        }

        if (!model.isCompleted) {
            OutlinedButton(
                onClick = { onOpenCourse(model.courseId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.learn_course_overview_action))
            }
        }

        AppFooterCard(
            text = if (model.isCompleted) {
                stringResource(R.string.learn_reader_footer_completed)
            } else {
                stringResource(R.string.learn_reader_footer_unfinished)
            }
        )
    }
}

@Composable
private fun LearnCourseCatalogCard(
    course: LearnCourseCardUiModel,
    onOpenCourse: (String) -> Unit
) {
    AppSurfaceCard(
        modifier = Modifier.testTag("learn_course_card_${course.id}"),
        onClick = { onOpenCourse(course.id) },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = course.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = course.subtitle,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = course.description,
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppMetricBadge(
                label = stringResource(R.string.learn_course_sections_badge),
                value = stringResource(
                    R.string.learn_sections_progress,
                    course.availableSectionCount,
                    course.totalSectionCount
                )
            )
            AppMetricBadge(
                label = stringResource(R.string.learn_course_time_badge),
                value = stringResource(R.string.learn_minutes_short, course.estimatedMinutes)
            )
        }
        Text(
            text = stringResource(R.string.learn_open_course_action),
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
