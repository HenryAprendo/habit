package com.henrydev.habit.ui.screen.challenges

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.R
import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.model.ChallengeProgress
import com.henrydev.habit.domain.model.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    onNavigateToPaywall: () -> Unit,
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val habits by viewModel.availableHabits.collectAsStateWithLifecycle()

    // Local state for the selection dialog
    var selectedChallenge by remember { mutableStateOf<Challenge?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Local Scroll Behavior for collapsing TopBar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(uiState.joinStatus) {
        if (uiState.joinStatus is JoinChallengeStatus.ProRequired) {
            onNavigateToPaywall()
            viewModel.resetJoinStatus()
        }
    }

    if (showDialog && selectedChallenge != null) {
        HabitSelectionDialog(
            habits = habits,
            onDismiss = { showDialog = false },
            onHabitSelected = { habitId ->
                viewModel.joinChallenge(selectedChallenge!!, habitId)
                showDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.challenges_faith_challenges_title),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(top =innerPadding.calculateTopPadding())) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                ChallengesList(
                    challenges = uiState.challenges,
                    progressMap = uiState.progressMap,
                    onJoinClick = { challenge ->
                        selectedChallenge = challenge
                        showDialog = true
                    },
                    joinStatus = uiState.joinStatus,
                    isUserPro = uiState.isUserPro
                )
            }
        }
    }


}

@Composable
fun HabitSelectionDialog(
    habits: List<Habit>,
    onDismiss: () -> Unit,
    onHabitSelected: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.challenges_choose_discipline_dialog)) },
        text = {
            if (habits.isEmpty()) {
                Text(stringResource(R.string.challenges_no_habits_msg))
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(habits, key = { it.id }) { habit ->
                        ListItem(
                            headlineContent = { Text(habit.name) },
                            modifier = Modifier.clickable { onHabitSelected(habit.id) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.challenges_not_now)) }
        }
    )
}

@Composable
private fun ChallengesList(
    challenges: List<Challenge>,
    onJoinClick: (Challenge) -> Unit,
    progressMap: Map<Long,ChallengeProgress>,
    joinStatus: JoinChallengeStatus,
    isUserPro: Boolean
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(challenges, key = { it.id }) { challenge ->
            ChallengeItem(
                challenge = challenge,
                onJoinClick = { onJoinClick(challenge) },
                isLoading = joinStatus is JoinChallengeStatus.Loading,
                progress = progressMap[challenge.id],
                isUserPro = isUserPro
            )
        }
    }
}

@Composable
private fun ChallengeItem(
    challenge: Challenge,
    progress: ChallengeProgress?,
    onJoinClick: () -> Unit,
    isLoading: Boolean,
    isUserPro: Boolean,
) {
    val isJoined = progress != null && progress.linkedHabitId != 0L
    // Lógica de finalización: ¿Llegó a la meta?
    val isCompleted = progress != null && progress.completedDays >= progress.totalDays

    val cardColor = when {
        isCompleted -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        challenge.isPro && isUserPro -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (isCompleted) stringResource(R.string.challenges_mission_accomplished) else challenge.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Icono dinámico: Estrella para Pro, Check para completado
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else if (challenge.isPro) {
                    Icon(
                        imageVector = if (isUserPro) Icons.Default.CheckCircle else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isUserPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Progress Section
            if (isJoined) {
                Spacer(modifier = Modifier.height(20.dp))
                LinearProgressIndicator(
                    progress = { progress.progressPercentage },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    strokeCap = StrokeCap.Round,
                    color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isCompleted)
                        stringResource(R.string.challenges_faithful_to_end)
                    else
                        stringResource(R.string.challenges_faithful_days_count, progress.completedDays, progress.totalDays),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.challenges_walk_duration, challenge.durationDays),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )

                Button(
                    onClick = onJoinClick,
                    enabled = !isLoading && !isJoined && !isCompleted,
                    colors = if (isCompleted)
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    else ButtonDefaults.buttonColors()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        val buttonText = when {
                            isCompleted -> stringResource(R.string.challenges_status_finished)
                            isJoined -> stringResource(R.string.challenges_status_on_walk)
                            challenge.isPro && !isUserPro -> stringResource(R.string.challenges_status_advanced)
                            else -> stringResource(R.string.challenges_status_start)
                        }
                        Text(buttonText)
                    }
                }
            }
        }
    }
}