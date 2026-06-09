package com.henrydev.faithsteward.ui.screen.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.faithsteward.R
import com.henrydev.faithsteward.domain.model.HabitProgressDetail
import com.henrydev.faithsteward.domain.subscription.model.HabitStats
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = hiltViewModel()
){
    val uiState: ProgressUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Local Scroll Behavior for collapsing TopBar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.progress_spiritual_growth_title),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
            when (val state = uiState) {
                is ProgressUiState.Loading -> LoadingComponent()
                is ProgressUiState.Empty -> EmptyStatsComponent()
                is ProgressUiState.Success -> {
                    ProgressContent(
                        stats = state.stats,
                        habitsProgress = state.habitsProgress,
                        isPro = state.isPro,
                        onLockedClick = onNavigateToPaywall,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressContent(
    stats: HabitStats,
    habitsProgress: List<HabitProgressDetail>,
    isPro: Boolean,
    onLockedClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val completionPercentage = (stats.totalCompletionRate * 100).toInt()
            val faithfulnessLabel = when {
                completionPercentage >= 80 -> stringResource(R.string.progress_label_fruitful)
                completionPercentage >= 50 -> stringResource(R.string.progress_label_steadfast)
                completionPercentage >= 1 -> stringResource(R.string.progress_label_growing)
                else -> stringResource(R.string.progress_label_new_beginning)
            }

            StatCard(
                title = stringResource(R.string.progress_stat_faithfulness),
                value = "$completionPercentage%",
                subTitle = faithfulnessLabel,
                icon = Icons.Default.AutoGraph,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.progress_stat_endurance),
                value = stringResource(R.string.home_streak_days_plural, stats.bestStreakRecord),
                subTitle = stringResource(R.string.progress_label_firm_discipline),
                icon = Icons.Default.Whatshot,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Text(
            text = stringResource(R.string.progress_consistency_heart),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        MonthlyHeatmap(
            heatmapData = stats.heatmapData,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.progress_spiritual_disciplines),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (!isPro) {
            // Show a "Teaser" for Free users
            Text(
                text = stringResource(R.string.progress_pro_teaser),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Optional: Show a blurred or locked example card
        }

        habitsProgress.forEachIndexed { index, progress ->
            val isLocked = !isPro && index > 0
            HabitProgressItem(
                progress = progress,
                isPro = isPro,
                isLocked = isLocked,
                onClick = { if (isLocked || !isPro) onLockedClick() }
            )
        }

        StatCard(
            title = stringResource(R.string.progress_days_in_presence),
            value = stringResource(R.string.progress_days_completed_count, stats.perfectDaysCount),
            icon = Icons.Default.CalendarMonth,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@Composable
fun HabitProgressItem(
    progress: HabitProgressDetail,
    isPro: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val contentAlpha = if (isLocked) 0.5f else 1f
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            .graphicsLayer(alpha = contentAlpha)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    // If locked, we hide the real name to pique curiosity
                    text = if (isLocked) stringResource(R.string.progress_locked_discipline) else progress.habitName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = if (isLocked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )

                if (isLocked) {
                    // Visual "Gatekeeper": Shows a lock instead of the activity dots
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    // Weekly Trend: Only visible for the free item or if user is Pro
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        progress.lastSevenDays.forEach { day ->
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(
                                        if (day.isCompleted) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Metrics Grid: Rigorous lock logic for spiritual disciplines
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Firmness (Streaks)
                StatMiniItem(
                    label = stringResource(R.string.progress_metric_firmness),
                    // If locked, we hide the streak to encourage the upgrade
                    value = if (isLocked) "--" else "${progress.currentStreak}d",
                    subLabel = if (isLocked) stringResource(R.string.progress_pro_feature) else stringResource(R.string.progress_best_streak,progress.bestStreak),
                    icon = Icons.Default.Whatshot,
                    isPro = !isLocked // We pass false to grey out the icon
                )

                // Metric 2: Faithfulness (Rate)
                StatMiniItem(
                    label = stringResource(R.string.progress_metric_faithfulness),
                    // If locked, we hide the completion percentage
                    value = if (isLocked) "--" else "${(progress.completionRate * 100).toInt()}%",
                    subLabel = if (isLocked) stringResource(R.string.progress_unlock_analysis) else "Total: ${progress.totalCompletions}",
                    icon = Icons.Default.CheckCircle,
                    isPro = !isLocked
                )
            }

        }
    }

}

@Composable
private fun StatMiniItem(
    label: String,
    value: String,
    subLabel: String,
    icon: ImageVector,
    isPro: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = subLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subTitle: String? = null,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            subTitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}



@Composable
fun MonthlyHeatmap(
    heatmapData: Map<Long, Boolean>,
    modifier: Modifier = Modifier
) {
    val locale = Locale.getDefault()
    val firstDayOfWeek = remember(locale) { WeekFields.of(locale).firstDayOfWeek }
    val today = remember { LocalDate.now() }

    // Weekday header labels ordered by the locale's first day of week.
    val weekdayLabels = remember(firstDayOfWeek, locale) {
        (0 until 7).map { offset ->
            firstDayOfWeek.plus(offset.toLong()).getDisplayName(TextStyle.NARROW, locale)
        }
    }

    // Weekday-aligned grid: last 5 weeks ending with the current week.
    val weeks = remember(today, firstDayOfWeek) {
        val weeksToShow = 5
        val todayColumn = ((today.dayOfWeek.value - firstDayOfWeek.value) + 7) % 7
        val currentWeekStart = today.minusDays(todayColumn.toLong())
        val gridStart = currentWeekStart.minusWeeks((weeksToShow - 1).toLong())
        (0 until weeksToShow).map { week ->
            (0 until 7).map { day -> gridStart.plusWeeks(week.toLong()).plusDays(day.toLong()) }
        }
    }

    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Weekday headers, aligned to the day columns below.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekdayLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            weeks.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    week.forEach { date ->
                        val isFuture = date.isAfter(today)
                        val isActive = heatmapData[date.toEpochDay()] ?: false
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(
                                    when {
                                        isFuture -> Color.Transparent
                                        isActive -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HeatmapLegend()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.progress_last_month_walk),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun HeatmapLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(
            color = MaterialTheme.colorScheme.primary,
            label = stringResource(R.string.progress_legend_done)
        )
        LegendItem(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            label = stringResource(R.string.progress_legend_none)
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyStatsComponent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoGraph,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.progress_empty_state),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun LoadingComponent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}
