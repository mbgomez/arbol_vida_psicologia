package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.components.AppHeroCard
import com.netah.hakkam.numyah.mind.ui.components.AppMetricBadge
import com.netah.hakkam.numyah.mind.ui.components.AppScreenColumn
import com.netah.hakkam.numyah.mind.ui.components.AppSurfaceCard
import com.netah.hakkam.numyah.mind.viewmodel.HistorySessionUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiModel
import com.netah.hakkam.numyah.mind.viewmodel.HistoryUiState
import com.netah.hakkam.numyah.mind.viewmodel.HistoryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun HistoryRoute(
    paddingValues: PaddingValues,
    onOpenAssessment: (Long) -> Unit,
    onOpenAssessments: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    HistoryScreen(
        paddingValues = paddingValues,
        uiState = uiState,
        onOpenAssessment = onOpenAssessment,
        onOpenAssessments = onOpenAssessments
    )
}

@Composable
fun HistoryScreen(
    paddingValues: PaddingValues,
    uiState: HistoryUiState,
    onOpenAssessment: (Long) -> Unit,
    onOpenAssessments: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            HistoryUiState.Loading -> HistoryMessageState(
                paddingValues = paddingValues,
                title = stringResource(R.string.history_loading_title),
                body = stringResource(R.string.history_loading_body)
            )

            HistoryUiState.Empty -> HistoryMessageState(
                paddingValues = paddingValues,
                title = stringResource(R.string.history_empty_title),
                body = stringResource(R.string.history_empty_body),
                actionLabel = stringResource(R.string.history_empty_action),
                onAction = onOpenAssessments
            )

            HistoryUiState.Error -> HistoryMessageState(
                paddingValues = paddingValues,
                title = stringResource(R.string.history_error_title),
                body = stringResource(R.string.history_error_body),
                actionLabel = stringResource(R.string.history_error_action),
                onAction = onOpenAssessments
            )

            is HistoryUiState.Loaded -> HistoryLoadedState(
                paddingValues = paddingValues,
                model = uiState.model,
                onOpenAssessment = onOpenAssessment
            )
        }
    }
}

@Composable
private fun HistoryLoadedState(
    paddingValues: PaddingValues,
    model: HistoryUiModel,
    onOpenAssessment: (Long) -> Unit
) {
    AppScreenColumn(
        paddingValues = paddingValues,
        modifier = Modifier.testTag("history_list")
    ) {
        AppHeroCard(
            eyebrow = stringResource(R.string.history_overview_eyebrow),
            title = stringResource(R.string.history_overview_title),
            body = stringResource(
                R.string.history_overview_body,
                model.totalSessions,
                model.questionnaireTitle
            )
        )

        Text(
            text = stringResource(R.string.history_list_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.history_list_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        model.sessions.forEach { session ->
            HistorySessionCard(
                model = session,
                onOpenAssessment = { onOpenAssessment(session.sessionId) }
            )
        }
    }
}

@Composable
private fun HistorySessionCard(
    model: HistorySessionUiModel,
    onOpenAssessment: () -> Unit
) {
    val contentSpacing = dimensionResource(R.dimen.screen_section_spacing)
    val textSpacing = dimensionResource(R.dimen.spacing_sm)
    val badgeSpacing = dimensionResource(R.dimen.spacing_md)

    AppSurfaceCard(
        modifier = Modifier.testTag("history_session_${model.sessionId}"),
        onClick = onOpenAssessment,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        elevation = dimensionResource(R.dimen.elevation_none)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(textSpacing)
            ) {
                Text(
                    text = stringResource(R.string.history_card_eyebrow),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(
                        R.string.history_completed_at,
                        formatHistoryDateTime(model.completedAt)
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(
                        R.string.history_started_at,
                        formatHistoryDateTime(model.startedAt)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(badgeSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppMetricBadge(
                    label = stringResource(R.string.history_session_count_label),
                    value = stringResource(
                        R.string.history_session_count_value,
                        model.completedCount,
                        model.totalCount
                    )
                )
                model.needsAttentionImbalancePercent?.let { imbalance ->
                    AppMetricBadge(
                        label = stringResource(R.string.history_tension_label),
                        value = stringResource(R.string.results_imbalance_badge, imbalance)
                    )
                }
            }

            model.needsAttentionSephiraName?.let { sephiraName ->
                Text(
                    text = stringResource(
                        R.string.history_needs_attention_summary,
                        sephiraName
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            model.mostBalancedSephiraName?.let { sephiraName ->
                Text(
                    text = stringResource(
                        R.string.history_most_balanced_summary,
                        sephiraName,
                        model.mostBalancedBalancePercent ?: 0
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onOpenAssessment,
                modifier = Modifier.fillMaxWidth().testTag("history_open_${model.sessionId}")
            ) {
                Text(text = stringResource(R.string.history_open_action))
            }
        }
    }
}

@Composable
private fun HistoryMessageState(
    paddingValues: PaddingValues,
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    AppScreenColumn(paddingValues = paddingValues) {
        AppHeroCard(
            eyebrow = stringResource(R.string.history_overview_eyebrow),
            title = title,
            body = body
        )
        if (actionLabel != null && onAction != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth().testTag("history_primary_action")
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

private fun formatHistoryDateTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return formatter.format(
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
    )
}
