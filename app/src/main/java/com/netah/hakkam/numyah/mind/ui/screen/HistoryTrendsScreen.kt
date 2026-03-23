package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppMetricBadge
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSectionCard
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.viewmodel.HistoryDeepTrendsUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesChartUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTimeSeriesLineUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendExploreMode
import com.netah.hakkam.numyah.mind.viewmodel.HistoryTrendScoreType
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiState
import com.netah.hakkam.numyah.mind.viewmodel.HistoryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.max

@Composable
fun HistoryTrendsRoute(
    paddingValues: PaddingValues,
    onOpenAssessments: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    HistoryTrendsScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onOpenAssessments = onOpenAssessments,
        onRetry = viewModel::retry
    )
}

@Composable
fun HistoryTrendsScreen(
    paddingValues: PaddingValues,
    uiState: HistoryUiState,
    onOpenAssessments: () -> Unit,
    onRetry: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            HistoryUiState.Loading -> HistoryTrendsLoadingState(
                paddingValues = paddingValues,
                onOpenAssessments = onOpenAssessments
            )

            HistoryUiState.Empty -> HistoryTrendsEmptyState(
                paddingValues = paddingValues,
                onOpenAssessments = onOpenAssessments
            )

            HistoryUiState.Error -> HistoryTrendsErrorState(
                paddingValues = paddingValues,
                onRetry = onRetry,
                onOpenAssessments = onOpenAssessments
            )

            is HistoryUiState.Loaded -> HistoryTrendsLoadedState(
                paddingValues = paddingValues,
                model = uiState.model.deeperTrends
            )
        }
    }
}

@Composable
private fun HistoryTrendsLoadedState(
    paddingValues: PaddingValues,
    model: HistoryDeepTrendsUiModel
) {
    var selectedModeName by rememberSaveable {
        mutableStateOf(HistoryTrendExploreMode.BY_SEPHIRA.name)
    }
    var selectedSephiraName by rememberSaveable {
        mutableStateOf(model.defaultSephiraId?.name.orEmpty())
    }
    var selectedScoreTypeName by rememberSaveable {
        mutableStateOf(model.defaultScoreType.name)
    }
    var enabledScoreTypeSephiraNames by rememberSaveable {
        mutableStateOf(model.sephiraOptions.map { it.sephiraId.name })
    }

    val selectedMode = runCatching {
        HistoryTrendExploreMode.valueOf(selectedModeName)
    }.getOrDefault(HistoryTrendExploreMode.BY_SEPHIRA)
    val selectedSephiraOption = model.sephiraOptions.firstOrNull {
        it.sephiraId.name == selectedSephiraName
    } ?: model.sephiraOptions.firstOrNull()
    val selectedScoreType = runCatching {
        HistoryTrendScoreType.valueOf(selectedScoreTypeName)
    }.getOrDefault(model.defaultScoreType)
    val chartModel = when (selectedMode) {
        HistoryTrendExploreMode.BY_SEPHIRA -> {
            selectedSephiraOption?.sephiraId?.let(model.bySephiraCharts::get)
        }

        HistoryTrendExploreMode.BY_SCORE_TYPE -> model.byScoreTypeCharts[selectedScoreType]
            ?.filteredByVisibleSephirot(enabledScoreTypeSephiraNames.toSet())
    }

    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag("history_trends_screen")
    ) {
        AppHeroCard(
            eyebrow = stringResource(R.string.history_trends_detail_eyebrow),
            title = stringResource(R.string.history_trends_detail_title),
            body = stringResource(
                R.string.history_trends_detail_body,
                model.sessionCount
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
        ) {
            AppMetricBadge(
                label = stringResource(R.string.history_trends_sessions_badge_label),
                value = stringResource(R.string.history_trends_sessions_badge_value, model.sessionCount)
            )
            AppMetricBadge(
                label = stringResource(R.string.history_trends_status_badge_label),
                value = stringResource(
                    if (model.hasComparisonData) {
                        R.string.history_trends_status_badge_ready
                    } else {
                        R.string.history_trends_status_badge_growing
                    }
                )
            )
        }

        if (!model.hasComparisonData) {
            AppSurfaceCard(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f),
                elevation = 0.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.spacing_sm)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.history_trends_detail_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.history_trends_detail_empty_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AppSectionCard(
            title = stringResource(R.string.history_trends_mode_title),
            body = stringResource(R.string.history_trends_mode_body),
            modifier = Modifier.testTag("history_trends_mode_section")
        ) {
            TabRow(
                selectedTabIndex = selectedMode.ordinal,
                modifier = Modifier.testTag("history_trends_mode_tabs")
            ) {
                HistoryTrendExploreMode.values().forEach { mode ->
                    Tab(
                        selected = selectedMode == mode,
                        onClick = { selectedModeName = mode.name },
                        modifier = Modifier.testTag("history_trends_mode_${mode.name.lowercase()}"),
                        text = {
                            Text(
                                text = stringResource(mode.titleRes()),
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }

        when (selectedMode) {
            HistoryTrendExploreMode.BY_SEPHIRA -> {
                AppSectionCard(
                    title = stringResource(R.string.history_trends_by_sephira_title),
                    body = stringResource(R.string.history_trends_by_sephira_body)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .testTag("history_trends_sephira_selector"),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_sm)
                        )
                    ) {
                        model.sephiraOptions.forEach { option ->
                            FilterChip(
                                selected = option.sephiraId == selectedSephiraOption?.sephiraId,
                                onClick = { selectedSephiraName = option.sephiraId.name },
                                label = { Text(option.displayName) },
                                modifier = Modifier.testTag(
                                    "history_trends_sephira_${option.sephiraId.name}"
                                )
                            )
                        }
                    }
                }
            }

            HistoryTrendExploreMode.BY_SCORE_TYPE -> {
                AppSectionCard(
                    title = stringResource(R.string.history_trends_by_score_type_title),
                    body = stringResource(R.string.history_trends_by_score_type_body)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .testTag("history_trends_score_type_selector"),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_sm)
                        )
                    ) {
                        HistoryTrendScoreType.values().forEach { scoreType ->
                            FilterChip(
                                selected = scoreType == selectedScoreType,
                                onClick = { selectedScoreTypeName = scoreType.name },
                                label = { Text(stringResource(scoreType.titleRes())) },
                                modifier = Modifier.testTag(
                                    "history_trends_score_type_${scoreType.name}"
                                )
                            )
                        }
                    }
                }

                AppSectionCard(
                    title = stringResource(R.string.history_trends_visible_sephirot_title),
                    body = stringResource(R.string.history_trends_visible_sephirot_body),
                    modifier = Modifier.testTag("history_trends_visibility_section")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .testTag("history_trends_visibility_selector"),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_sm)
                        )
                    ) {
                        model.sephiraOptions.forEach { option ->
                            val isEnabled = option.sephiraId.name in enabledScoreTypeSephiraNames
                            FilterChip(
                                selected = isEnabled,
                                onClick = {
                                    enabledScoreTypeSephiraNames = if (isEnabled) {
                                        enabledScoreTypeSephiraNames.filterNot {
                                            it == option.sephiraId.name
                                        }
                                    } else {
                                        enabledScoreTypeSephiraNames + option.sephiraId.name
                                    }
                                },
                                label = { Text(option.displayName) },
                                modifier = Modifier.testTag(
                                    "history_trends_visibility_${option.sephiraId.name}"
                                )
                            )
                        }
                    }
                }
            }
        }

        if (chartModel != null && chartModel.lines.isNotEmpty()) {
            AppSectionCard(
                title = when (selectedMode) {
                    HistoryTrendExploreMode.BY_SEPHIRA -> stringResource(
                        R.string.history_trends_chart_by_sephira_title,
                        selectedSephiraOption?.displayName.orEmpty()
                    )

                    HistoryTrendExploreMode.BY_SCORE_TYPE -> stringResource(
                        R.string.history_trends_chart_by_score_type_title,
                        stringResource(selectedScoreType.titleRes())
                    )
                },
                body = when (selectedMode) {
                    HistoryTrendExploreMode.BY_SEPHIRA -> stringResource(
                        R.string.history_trends_chart_by_sephira_body,
                        selectedSephiraOption?.displayName.orEmpty()
                    )

                    HistoryTrendExploreMode.BY_SCORE_TYPE -> stringResource(
                        R.string.history_trends_chart_by_score_type_body,
                        stringResource(selectedScoreType.titleRes())
                    )
                }
            ) {
                TrendLegend(
                    mode = selectedMode,
                    chartModel = chartModel
                )
                Text(
                    text = stringResource(
                        when (selectedMode) {
                            HistoryTrendExploreMode.BY_SEPHIRA -> R.string.history_trends_chart_helper_by_sephira
                            HistoryTrendExploreMode.BY_SCORE_TYPE -> R.string.history_trends_chart_helper_by_score_type
                        }
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HistoryLineChart(
                    mode = selectedMode,
                    chartModel = chartModel,
                    modifier = Modifier.testTag("history_trends_chart")
                )
            }

            AppSectionCard(
                title = stringResource(R.string.history_trends_points_title),
                body = stringResource(R.string.history_trends_points_body),
                modifier = Modifier.testTag("history_trends_session_details")
            ) {
                chartSessionRows(chartModel).forEach { sessionRow ->
                    AppSurfaceCard(
                        modifier = Modifier.testTag("history_trends_session_${sessionRow.sessionId}"),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f),
                        elevation = 0.dp
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                dimensionResource(R.dimen.spacing_md)
                            )
                        ) {
                            Text(
                                text = formatHistoryDate(sessionRow.completedAt),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            sessionRow.values.forEach { valueRow ->
                                TrendValueRow(
                                    modifier = Modifier.testTag(
                                        "history_trends_value_${sessionRow.sessionId}_${valueRow.id}"
                                    ),
                                    label = valueRow.label,
                                    value = valueRow.value?.let {
                                        stringResource(R.string.results_percent_value, it)
                                    } ?: stringResource(R.string.history_trends_missing_value)
                                )
                            }
                        }
                    }
                }
            }
        } else if (selectedMode == HistoryTrendExploreMode.BY_SCORE_TYPE) {
            AppSurfaceCard(
                modifier = Modifier.testTag("history_trends_chart_empty"),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f),
                elevation = 0.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.spacing_sm)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.history_trends_chart_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.history_trends_chart_empty_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendLegend(
    mode: HistoryTrendExploreMode,
    chartModel: HistoryTimeSeriesChartUiModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.spacing_sm)
        )
    ) {
        chartModel.lines.forEachIndexed { index, line ->
            TrendLegendItem(
                label = when (mode) {
                    HistoryTrendExploreMode.BY_SEPHIRA -> stringResource(
                        line.scoreType?.titleRes() ?: R.string.history_trends_missing_value
                    )

                    HistoryTrendExploreMode.BY_SCORE_TYPE -> line.displayName.orEmpty()
                },
                color = trendLineColor(
                    mode = mode,
                    line = line,
                    index = index
                ),
                valueSummary = line.points.lastOrNull()?.value?.let {
                    stringResource(R.string.history_trends_legend_value, it)
                }
            )
        }
    }
}

@Composable
private fun TrendLegendItem(
    label: String,
    color: Color,
    valueSummary: String?
) {
    val markerSize = dimensionResource(R.dimen.size_dot_md)
    val contentSpacing = dimensionResource(R.dimen.spacing_sm)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Canvas(
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.spacing_xs))
                    .size(markerSize)
            ) {
                drawCircle(
                    color = color,
                    radius = size.minDimension / 2f
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (valueSummary != null) {
            Text(
                text = valueSummary,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryLineChart(
    mode: HistoryTrendExploreMode,
    chartModel: HistoryTimeSeriesChartUiModel,
    modifier: Modifier = Modifier
) {
    val chartHeight = 220.dp
    val strokeWidth = 3.dp
    val pointRadius = 4.dp
    val axisLabelPadding = dimensionResource(R.dimen.spacing_sm)
    val axisColumnWidth = 36.dp
    val dateTextStyle = MaterialTheme.typography.labelMedium
    val firstPoint = chartModel.lines.firstOrNull()?.points?.firstOrNull()
    val lastPoint = chartModel.lines.firstOrNull()?.points?.lastOrNull()
    val baselineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    val lineColors = chartModel.lines.mapIndexed { index, line ->
        trendLineColor(mode = mode, line = line, index = index)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(axisLabelPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(axisLabelPadding)
        ) {
            Column(
                modifier = Modifier.height(chartHeight).width(axisColumnWidth),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.results_percent_value, 100),
                    style = dateTextStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.results_percent_value, 50),
                    style = dateTextStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.results_percent_value, 0),
                    style = dateTextStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Canvas(
                modifier = modifier
                    .weight(1f)
                    .height(chartHeight)
            ) {
                val lineCount = max(chartModel.lines.maxOfOrNull { it.points.size } ?: 0, 1)
                val leftPadding = 8.dp.toPx()
                val rightPadding = 8.dp.toPx()
                val topPadding = 12.dp.toPx()
                val bottomPadding = 12.dp.toPx()
                val usableWidth = size.width - leftPadding - rightPadding
                val usableHeight = size.height - topPadding - bottomPadding

                listOf(0f, 0.5f, 1f).forEach { fraction ->
                    val y = topPadding + usableHeight * fraction
                    drawLine(
                        color = baselineColor,
                        start = Offset(leftPadding, y),
                        end = Offset(size.width - rightPadding, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                chartModel.lines.forEachIndexed { index, line ->
                    val color = lineColors[index]
                    val nonNullPoints = line.points.mapIndexedNotNull { pointIndex, point ->
                        point.value?.let { value ->
                            val x = if (lineCount == 1) {
                                leftPadding + usableWidth / 2f
                            } else {
                                leftPadding + (usableWidth * pointIndex / (lineCount - 1))
                            }
                            val y = topPadding + usableHeight - (usableHeight * value / 100f)
                            pointIndex to Offset(x, y)
                        }
                    }

                    if (nonNullPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(nonNullPoints.first().second.x, nonNullPoints.first().second.y)
                            nonNullPoints.drop(1).forEach { (_, offset) ->
                                lineTo(offset.x, offset.y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(width = strokeWidth.toPx())
                        )
                    }

                    nonNullPoints.forEach { (_, offset) ->
                        drawCircle(
                            color = color,
                            radius = pointRadius.toPx(),
                            center = offset
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = firstPoint?.let { formatHistoryDate(it.completedAt) }.orEmpty(),
                style = dateTextStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = lastPoint?.let { formatHistoryDate(it.completedAt) }.orEmpty(),
                style = dateTextStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TrendValueRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun trendLineColor(
    mode: HistoryTrendExploreMode,
    line: HistoryTimeSeriesLineUiModel,
    index: Int
): Color {
    return when (mode) {
        HistoryTrendExploreMode.BY_SEPHIRA -> when (line.scoreType) {
            HistoryTrendScoreType.BALANCE -> MaterialTheme.colorScheme.primary
            HistoryTrendScoreType.DEFICIENCY -> MaterialTheme.colorScheme.tertiary
            HistoryTrendScoreType.EXCESS -> MaterialTheme.colorScheme.secondary
            null -> MaterialTheme.colorScheme.outline
        }

        HistoryTrendExploreMode.BY_SCORE_TYPE -> {
            val palette = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary,
                lerp(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, 0.45f),
                lerp(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary, 0.45f),
                lerp(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary, 0.45f)
            )
            palette[index % palette.size].copy(alpha = if (index < palette.size) 1f else 0.82f)
        }
    }
}

private data class ChartSessionRow(
    val sessionId: Long,
    val completedAt: Long,
    val values: List<ChartValueRow>
)

private data class ChartValueRow(
    val id: String,
    val label: String,
    val value: Int?
)

@Composable
private fun chartSessionRows(chartModel: HistoryTimeSeriesChartUiModel): List<ChartSessionRow> {
    val firstLine = chartModel.lines.firstOrNull() ?: return emptyList()
    return firstLine.points.indices.map { index ->
        val point = firstLine.points[index]
        ChartSessionRow(
            sessionId = point.sessionId,
            completedAt = point.completedAt,
            values = chartModel.lines.map { line ->
                ChartValueRow(
                    id = line.id,
                    label = line.displayName
                        ?: line.scoreType?.let { stringResource(it.titleRes()) }
                        ?: "",
                    value = line.points.getOrNull(index)?.value
                )
            }
        )
    }.reversed()
}

private fun HistoryTrendExploreMode.titleRes(): Int {
    return when (this) {
        HistoryTrendExploreMode.BY_SEPHIRA -> R.string.history_trends_mode_by_sephira
        HistoryTrendExploreMode.BY_SCORE_TYPE -> R.string.history_trends_mode_by_score_type
    }
}

private fun HistoryTrendScoreType.titleRes(): Int {
    return when (this) {
        HistoryTrendScoreType.BALANCE -> R.string.assessment_score_balance
        HistoryTrendScoreType.DEFICIENCY -> R.string.assessment_score_deficiency
        HistoryTrendScoreType.EXCESS -> R.string.assessment_score_excess
    }
}

private fun HistoryTimeSeriesChartUiModel.filteredByVisibleSephirot(
    visibleSephiraNames: Set<String>
): HistoryTimeSeriesChartUiModel {
    return copy(
        lines = lines.filter { line ->
            val sephiraName = line.sephiraId?.name ?: return@filter true
            sephiraName in visibleSephiraNames
        }
    )
}

@Composable
private fun HistoryTrendsLoadingState(
    paddingValues: PaddingValues,
    onOpenAssessments: () -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        AppHeroCard(
            eyebrow = stringResource(R.string.history_trends_detail_eyebrow),
            title = stringResource(R.string.history_loading_title),
            body = stringResource(R.string.history_loading_body)
        )
        AppSectionCard(
            title = stringResource(R.string.history_trends_loading_card_title),
            body = stringResource(R.string.history_trends_loading_card_body),
            modifier = Modifier.testTag("history_trends_loading_card")
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_trends_loading_indicator"),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        OutlinedButton(
            onClick = onOpenAssessments,
            modifier = Modifier.fillMaxWidth().testTag("history_trends_secondary_action")
        ) {
            Text(text = stringResource(R.string.history_loading_secondary_action))
        }
    }
}

@Composable
private fun HistoryTrendsEmptyState(
    paddingValues: PaddingValues,
    onOpenAssessments: () -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        AppHeroCard(
            eyebrow = stringResource(R.string.history_trends_detail_eyebrow),
            title = stringResource(R.string.history_empty_title),
            body = stringResource(R.string.history_empty_body)
        )
        AppSectionCard(
            title = stringResource(R.string.history_trends_empty_card_title),
            body = stringResource(R.string.history_trends_empty_card_body),
            modifier = Modifier.testTag("history_trends_empty_card")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
            ) {
                HistoryTrendsSupportLine(stringResource(R.string.history_trends_empty_point_one))
                HistoryTrendsSupportLine(stringResource(R.string.history_trends_empty_point_two))
            }
        }
        Button(
            onClick = onOpenAssessments,
            modifier = Modifier.fillMaxWidth().testTag("history_trends_primary_action")
        ) {
            Text(text = stringResource(R.string.history_empty_action))
        }
    }
}

@Composable
private fun HistoryTrendsErrorState(
    paddingValues: PaddingValues,
    onRetry: () -> Unit,
    onOpenAssessments: () -> Unit
) {
    AppScreenColumn(paddingValues = paddingValues) {
        AppHeroCard(
            eyebrow = stringResource(R.string.history_trends_detail_eyebrow),
            title = stringResource(R.string.history_error_title),
            body = stringResource(R.string.history_error_body)
        )
        AppSectionCard(
            title = stringResource(R.string.history_trends_error_card_title),
            body = stringResource(R.string.history_trends_error_card_body),
            modifier = Modifier.testTag("history_trends_error_card")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .weight(1f)
                    .testTag("history_trends_retry_action")
            ) {
                Text(text = stringResource(R.string.history_retry_action))
            }
            OutlinedButton(
                onClick = onOpenAssessments,
                modifier = Modifier
                    .weight(1f)
                    .testTag("history_trends_primary_action")
            ) {
                Text(text = stringResource(R.string.history_error_action))
            }
        }
    }
}

@Composable
private fun HistoryTrendsSupportLine(text: String) {
    val markerColor = MaterialTheme.colorScheme.secondary
    val markerSize = dimensionResource(R.dimen.size_dot_sm)

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
        verticalAlignment = androidx.compose.ui.Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.spacing_xs_plus))
                .width(markerSize)
                .height(markerSize)
        ) {
            Canvas(modifier = Modifier.size(markerSize)) {
                drawCircle(
                    color = markerColor,
                    radius = size.minDimension / 2f
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatHistoryDate(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
    return formatter.format(
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
    )
}
