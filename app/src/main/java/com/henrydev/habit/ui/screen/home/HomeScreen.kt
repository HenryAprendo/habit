package com.henrydev.habit.ui.screen.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.HabitTopAppBar
import com.henrydev.habit.ui.navigation.HabitScreen
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeBody(
        uiState = uiState,
        onToggleHabitState = { habitId, currentStatus ->
            viewModel.toggleHabit(habitId,currentStatus)
        },
        onUpgradeClick = onNavigateToPaywall,
        modifier = modifier
    )

}

@Composable
fun HomeBody(
    uiState: HomeUiState,
    onToggleHabitState: (Int,Boolean) -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(uiState) {
        is HomeUiState.Empty -> EmptyComponent()
        is HomeUiState.Loading -> LoadingComponent()
        is HomeUiState.Error -> ErrorComponent(message = uiState.message)
        is HomeUiState.Success ->
            HabitsList(
                habits = uiState.habits,
                showAds = uiState.showAds,
                onToggleHabit = onToggleHabitState,
                onUpgradeClick = { },
                modifier = modifier
            )
    }
}

@Composable
fun HabitsList(
    habits: List<HabitItemState>,
    showAds: Boolean,
    onToggleHabit: (Int,Boolean) -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        items( items = habits, key = { it.habit.id }) { item ->
            HabitItem(
                itemState = item,
                onCheckedChange = { onToggleHabit(item.habit.id, item.isCompleted) }
            )
        }
        if (showAds) {
            item(key = "ad_banner") {
                AdBannerPlaceholder(onUpgradeClick = onUpgradeClick)
            }
        }
    }
}


@Composable
fun HabitItem(
    itemState: HabitItemState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (itemState.isCompleted) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CardScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (itemState.isCompleted) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        label = "CardColor"
    )

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
       Column(
           modifier = Modifier
               .padding(20.dp)
               .fillMaxWidth()
       ) {
           Row(
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.SpaceBetween,
               modifier = Modifier.fillMaxWidth()
           ) {
               Column(
                   modifier = Modifier.weight(1f)
               ) {
                   Text(
                       text = itemState.habit.name,
                       style = MaterialTheme.typography.titleLarge,
                       color = MaterialTheme.colorScheme.onSurface
                   )
                   StreakCounter(itemState.streakCounter)
                   if(itemState.habit.description.isNotBlank()) {
                       Text(
                           text = itemState.habit.description,
                           style = MaterialTheme.typography.bodyMedium,
                           color = MaterialTheme.colorScheme.onSurfaceVariant,
                           maxLines = 2
                       )
                   }
               }
               Checkbox(
                   checked = itemState.isCompleted,
                   onCheckedChange = onCheckedChange,
                   colors = CheckboxDefaults.colors(
                       checkedColor = MaterialTheme.colorScheme.primary,
                       uncheckedColor = MaterialTheme.colorScheme.outline
                   )
               )
           }
           Spacer(modifier = Modifier.height(20.dp))
           HorizontalDivider(
               modifier = Modifier.padding(bottom = 12.dp),
               thickness = 0.5.dp,
               color = MaterialTheme.colorScheme.outlineVariant
           )
           WeeklyTracker(days = itemState.weeklyProgress)
       }
    }
}

@Composable
fun  WeeklyTracker(
    days: List<HabitDayState>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { dayState ->
            DayIndicator(dayState)
        }
    }
}

@Composable
fun DayIndicator(
    dayState: HabitDayState,
    modifier: Modifier = Modifier
) {
    val dayInitial = remember(dayState.date) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dayState.date
        }
        calendar.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.SHORT,
            Locale.ENGLISH
        ).first().toString()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Text(
            text = dayInitial,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (dayState.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (dayState.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun StreakCounter(
    count: Int,
    modifier: Modifier = Modifier
) {
    val activeStreakColor = Color(0xFFFF9800)

    val iconScale by animateFloatAsState(
        targetValue = if (count > 0) 1.2f else 1f,
        animationSpec = repeatable(
            iterations = if (count > 0) 3 else 1,
            animation = tween(durationMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FireScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Whatshot,
            contentDescription = null,
            tint = if (count > 0) {
                //MaterialTheme.colorScheme.tertiary
                activeStreakColor
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5F)
            },
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        )
        Text(
            text = if (count == 1) {
                "1 day streak"
            } else {
                "$count days streak"
            },
            style = MaterialTheme.typography.labelMedium,
            color = if (count > 0) {
               //MaterialTheme.colorScheme.tertiary
                activeStreakColor
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun AdBannerPlaceholder(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info, // O un icono de "AD"
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sponsored Content",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
                Text(
                    text = "Remove all ads with Habit Pro",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            TextButton(onClick = onUpgradeClick) {
                Text("UPGRADE")
            }
        }
    }
}

@Composable
fun LoadingComponent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorComponent(message: String = "") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun EmptyComponent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Podrías añadir un Icono aquí
        Icon(
            imageVector = Icons.Default.Check, // O uno de "List"
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No habits yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Start your journey by adding your first daily goal.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



