package com.henrydev.habit.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.domain.model.Habit
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar( title = { Text("My Habits") })
        }
    ) { innerPadding ->

        Box(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            when(val state = uiState) {
                is HomeUiState.Empty ->
                    HabitsList(
                        habits = habitPreviewList,
                        onToggleHabit = { habitId, currentStatus ->
                            coroutineScope.launch {
                                viewModel.toggleHabit(habitId,currentStatus)
                            }
                        }
                    )
                is HomeUiState.Loading -> LoadingComponent()
                is HomeUiState.Error -> ErrorComponent()
                is HomeUiState.Success ->
                    HabitsList(
                        habits = state.habits,
                        onToggleHabit = { habitId, currentStatus ->
                            coroutineScope.launch {
                                viewModel.toggleHabit(habitId,currentStatus)
                            }
                        }
                    )
            }
        }

    }

}

@Composable
fun HabitsList(
    habits: List<HabitItemState>,
    onToggleHabit: (Int,Boolean) -> Unit,
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
    }
}

val habitPreviewList = listOf(
    HabitItemState(
        habit = Habit(1, "Beber Agua", "2 litros al día", 1, System.currentTimeMillis()),
        isCompleted = true
    ),
    HabitItemState(
        habit = Habit(2, "Hacer Ejercicio", "30 min de cardio", 1, System.currentTimeMillis()),
        isCompleted = false
    ),
    HabitItemState(
        habit = Habit(3, "Leer", "15 páginas de un libro", 1, System.currentTimeMillis()),
        isCompleted = true
    ),
    HabitItemState(
        habit = Habit(4, "Meditar", "10 minutos en la mañana", 1, System.currentTimeMillis()),
        isCompleted = false
    ),
    HabitItemState(
        habit = Habit(5, "Estudiar Kotlin", "Repasar Coroutines", 1, System.currentTimeMillis()),
        isCompleted = false
    ),
    HabitItemState(
        habit = Habit(6, "Caminar al perro", "Paseo por el parque", 1, System.currentTimeMillis()),
        isCompleted = true
    ),
    HabitItemState(
        habit = Habit(7, "Limpiar Escritorio", "Mantener ordenado", 1, System.currentTimeMillis()),
        isCompleted = false
    ),
    HabitItemState(
        habit = Habit(8, "Dormir temprano", "Antes de las 11 PM", 1, System.currentTimeMillis()),
        isCompleted = true
    ),
    HabitItemState(
        habit = Habit(9, "Escribir Diario", "Reflexión del día", 1, System.currentTimeMillis()),
        isCompleted = false
    ),
    HabitItemState(
        habit = Habit(10, "Llamar a Familia", "Charla semanal", 1, System.currentTimeMillis()),
        isCompleted = false
    )
)

@Composable
fun HabitItem(
    itemState: HabitItemState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = itemState.habit.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = itemState.habit.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = itemState.isCompleted,
                onCheckedChange = onCheckedChange
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
             horizontalAlignment = Alignment.CenterHorizontally
         ) {
             Text(
                 text = "No hay hábitos creados aún",
                 style = MaterialTheme.typography.headlineSmall
             )
             Spacer(modifier =  Modifier.padding(8.dp))
             Text(
                 text = "Comienza agregando uno nuevo",
                 style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )
         }
    }
}



