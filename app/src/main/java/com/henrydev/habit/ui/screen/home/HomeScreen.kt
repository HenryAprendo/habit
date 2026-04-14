package com.henrydev.habit.ui.screen.home

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.R
import com.henrydev.habit.domain.model.DailyDevotional
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.model.UserStats
import com.henrydev.habit.domain.use_cases.HabitDayState
import com.henrydev.habit.domain.use_cases.HabitItemState
import kotlinx.coroutines.launch
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
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val dailyDevotional by viewModel.dailyDevotional.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (actionState.showActionSheet) {
        HabitActionSheet(
            onDismiss = { viewModel.dismissAllActions() },
            onEdit = { viewModel.openEditDialog() },
            onDelete = { viewModel.openDeleteConfirmation() }
        )
    }
    // 2. Show Delete Confirmation if active
    if (actionState.showDeleteConfirmation) {
        DeleteHabitConfirmation(
            habitName = actionState.selectedHabit?.name ?: "",
            onDismiss = { viewModel.dismissAllActions() },
            onConfirm = { viewModel.deleteHabit() }
        )
    }

    if (actionState.showEditDialog) {
        actionState.selectedHabit?.let {
            EditHabitDialog(
                habit = it,
                onDismiss = { viewModel.dismissAllActions() },
                onConfirm = { newName, newDescription ->
                    viewModel.updateHabit(newName, newDescription)
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_disciplines_title),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        HomeBody(
            uiState = uiState,
            userStats = userStats,
            dailyDevotional = dailyDevotional,
            onToggleHabitState = { habitId, currentStatus ->
                viewModel.toggleHabit(habitId,currentStatus)

                if (!currentStatus) {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.well_done_stay_faithful),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            },
            onLongClick = { habit ->
                viewModel.onHabitLongClick(habit)
            },
            onUpgradeClick = onNavigateToPaywall,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }

}

@Composable
fun HomeBody(
    uiState: HomeUiState,
    userStats: UserStats?,
    dailyDevotional: DailyDevotional?,
    onToggleHabitState: (Long,Boolean) -> Unit,
    onLongClick: (Habit) -> Unit,
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
                userStats = userStats,
                dailyDevotional = dailyDevotional,
                onToggleHabit = onToggleHabitState,
                onLongClick = onLongClick,
                onUpgradeClick = onUpgradeClick,
                modifier = modifier
            )
    }
}

@Composable
fun HabitsList(
    habits: List<HabitItemState>,
    userStats: UserStats?,
    dailyDevotional: DailyDevotional?,
    showAds: Boolean = false,
    onToggleHabit: (Long,Boolean) -> Unit,
    onLongClick: (Habit) -> Unit,
    modifier: Modifier = Modifier,
    onUpgradeClick: () -> Unit = { },
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        item(key = "purpose_header") {
            PurposeHeader()
        }

        item(key = "daily_devotional") {
            dailyDevotional?.let { devotional ->
                DailyDevotionalCard(devotional = devotional)
            }
        }

        item(key = "progress_header") {
            userStats?.let { stats -> UserProgressHeader(stats = stats) }
        }

        items( items = habits, key = { it.habit.id }) { item ->
            HabitItem(
                itemState = item,
                onCheckedChange = { onToggleHabit(item.habit.id, item.isCompleted) },
                onLongClick = { onLongClick(item.habit) }

            )
        }
    }
}


@Composable
fun HabitItem(
    itemState: HabitItemState,
    onCheckedChange: (Boolean) -> Unit,
    onLongClick: () -> Unit,
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
            .combinedClickable(
                onClick = { onCheckedChange(itemState.isCompleted) },
                onLongClick = { onLongClick() }
            )
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
                   onCheckedChange = { onCheckedChange(itemState.isCompleted)},
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
        val localDate = java.time.Instant.ofEpochMilli(dayState.date)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()

        localDate.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.SHORT,
            java.util.Locale.getDefault()
        ).first().toString().uppercase()
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
                    contentDescription = null,
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
                stringResource(R.string.home_streak_day_singular)
            } else {
                stringResource(R.string.home_streak_days_plural, count)
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
fun UserProgressHeader(
    stats: UserStats,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = stats.progressToNextLevel,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "levelProgress"
    )

    ElevatedCard(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = stats.rankTitle.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stringResource(R.string.home_level_label, stats.level),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.home_total_points_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.home_total_xp_label, stats.totalXp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.home_progress_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(stats.progressToNextLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round
                )
            }

            Text(
                text = stringResource(R.string.home_next_rank_points, stats.xpRequiredForNext),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
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
            text = stringResource(R.string.home_no_habits_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.home_no_habits_subtitle),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitActionSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.home_action_edit)) },
                leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier.clickable { onEdit() }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.home_action_delete), color = MaterialTheme.colorScheme.error) },
                leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.clickable { onDelete() }
            )
        }
    }
}

@Composable
fun DeleteHabitConfirmation(
    habitName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.home_delete_confirm_title)) },
        text = { Text(stringResource(R.string.home_delete_confirm_msg, habitName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.home_delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.home_cancel))
            }
        }
    )
}

@Composable
fun EditHabitDialog(
    habit: Habit,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    // We initialize the state with current habit data
    var name by remember { mutableStateOf(habit.name) }
    var description by remember { mutableStateOf(habit.description) }
    val isNameValid = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.home_edit_dialog_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.home_edit_dialog_name_label)) },
                    placeholder = { Text(stringResource(R.string.home_edit_dialog_name_placeholder)) },
                    singleLine = true,
                    isError = !isNameValid,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.home_edit_dialog_desc_label)) },
                    placeholder = { Text(stringResource(R.string.home_edit_dialog_desc_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (isNameValid) onConfirm(name, description) },
                enabled = isNameValid
            ) {
                Text(stringResource(R.string.home_save_changes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.home_cancel))
            }
        }
    )
}


// 1. New Spiritual Purpose Header Component
@Composable
fun PurposeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.home_quote_spiritual),
            style = MaterialTheme.typography.titleMedium.copy(
                fontStyle = FontStyle.Italic,
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        // Subtle divider to separate purpose from the progress card
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun DailyDevotionalCard(
    devotional: DailyDevotional,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shareFooter = stringResource(R.string.home_share_footer)
    val shareChooserTitle = stringResource(R.string.home_share_chooser_title)

    // Function to share the spiritual message
    val onShareClick = {
        val shareText = """
            “${devotional.verse}”
            — ${devotional.reference}
            
            ${devotional.reflection}
            
            $shareFooter
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, shareChooserTitle))
    }

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Share Icon - Positioned discreetly in the top right
            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.home_share_devotional_content_desc),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Verse
                Text(
                    text = "“${devotional.verse}”",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 28.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Reference
                Text(
                    text = devotional.reference.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(
                    modifier = Modifier.width(40.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Reflection
                Text(
                    text = devotional.reflection,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
