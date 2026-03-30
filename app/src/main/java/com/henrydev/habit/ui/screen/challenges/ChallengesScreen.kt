package com.henrydev.habit.ui.screen.challenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.model.Habit

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

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            ChallengesList(
                challenges = uiState.challenges,
                onJoinClick = { challenge ->
                    selectedChallenge = challenge
                    showDialog = true
                },
                joinStatus = uiState.joinStatus
            )
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
        title = { Text("Select a Habit for this Challenge") },
        text = {
            if (habits.isEmpty()) {
                Text("You need to create a habit first to join a challenge.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(habits) { habit ->
                        ListItem(
                            headlineContent = { Text(habit.name) },
                            modifier = Modifier.clickable { onHabitSelected(habit.id) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ChallengesList(
    challenges: List<Challenge>,
    onJoinClick: (Challenge) -> Unit,
    joinStatus: JoinChallengeStatus
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
                isLoading = joinStatus is JoinChallengeStatus.Loading
            )
        }
    }
}

@Composable
private fun ChallengeItem(
    challenge: Challenge,
    onJoinClick: () -> Unit,
    isLoading: Boolean
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = challenge.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                if (challenge.isPro) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Premium",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = challenge.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = challenge.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${challenge.durationDays} days", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Button(onClick = onJoinClick, enabled = !isLoading) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(if (challenge.isPro) "Join PRO" else "Accept Challenge")
                    }
                }
            }
        }
    }
}