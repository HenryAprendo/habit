package com.henrydev.habit.ui.screen.progress

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.domain.model.HabitProgressDetail
import com.henrydev.habit.domain.subscription.model.HabitStats
import com.henrydev.habit.ui.screen.home.AdBannerPlaceholder
import java.util.Calendar

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
                        text = "Spiritual Growth", // Aligned with your identity choice
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
            StatCard(
                title = "Daily Devotion",
                value = "${(stats.totalCompletionRate * 100).toInt()}%",
                icon = Icons.Default.AutoGraph,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Endurance Streak",
                value = "${stats.bestStreakRecord} Days",
                icon = Icons.Default.Whatshot,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Text(
            text = "Consistency of Heart",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        MonthlyHeatmap(
            heatmapData = stats.heatmapData,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isPro) {
            Spacer(modifier = Modifier.height(16.dp))

            // Reusing your existing Ad Component
            AdBannerPlaceholder(
                onUpgradeClick = onLockedClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Spiritual Disciplines",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (!isPro) {
            // Show a "Teaser" for Free users
            Text(
                text = "Go deeper into your spiritual analysis. Unlock insights to strengthen your walk with God",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Optional: Show a blurred or locked example card
        }

        habitsProgress.forEach { progress ->
            HabitProgressItem(
                progress = progress,
                isPro = isPro,
                onClick = { if(!isPro) onLockedClick() }
            )
        }

        StatCard(
            title = "Days in the Presence",
            value = "${stats.perfectDaysCount} Days Completed",
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
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header: Name and 7-day Sparkline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = progress.habitName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Weekly Trend: Mini Sparkline
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    progress.lastSevenDays.forEach { day ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(
                                    if (day.isCompleted && isPro) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Metrics Grid: Using all HabitProgressDetail parameters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Current vs Best Streak
                StatMiniItem(
                    label = "Firmness",
                    value = if (isPro) "${progress.currentStreak}d" else "--",
                    subLabel = "Best: ${progress.bestStreak}d",
                    icon = Icons.Default.Whatshot, // Ensure you have Icons.Default.Whatshot
                    isPro = isPro
                )

                // Metric 2: Completion Rate
                StatMiniItem(
                    label = "Faithfulness",
                    value = if (isPro) "${(progress.completionRate * 100).toInt()}%" else "--",
                    subLabel = "Total: ${progress.totalCompletions}",
                    icon = Icons.Default.CheckCircle,
                    isPro = isPro
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
            contentDescription = "$label metric",
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
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = "Icon for $title", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MonthlyHeatmap(
    heatmapData: Map<Long, Boolean>,
    modifier: Modifier = Modifier
) {
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }.timeInMillis
    }

    val last35Days = remember(heatmapData) {
        (34 downTo 0).map { dayOffset ->
            today - (dayOffset * 24 * 60 * 60 * 1000L)
        }
    }

    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                items(last35Days.size) { index ->
                    val timestamp = last35Days[index]
                    val isActive = heatmapData[timestamp] ?: false
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your walk over the last month",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun EmptyStatsComponent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Begin your spiritual walk. Complete your first discipline to see your progress")
    }
}

@Composable
fun LoadingComponent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}