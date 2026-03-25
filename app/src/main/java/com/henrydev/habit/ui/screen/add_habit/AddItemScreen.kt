package com.henrydev.habit.ui.screen.add_habit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.HabitTopAppBar
import com.henrydev.habit.ui.navigation.HabitScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddItemViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    //Lógica de cierre automático tras guardado exitoso
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            onNavigateBack()
        }
    }

    AddItemBody(
        habitDetail = uiState.habitDetail,
        isEntryValid = uiState.isEntryValid,
        isSaving = uiState.isSaving,
        onValueChange = { viewModel.onChangeForm(it) },
        onSaveClick = { viewModel.insertHabit() },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun AddItemBody(
    habitDetail: HabitDetail,
    isEntryValid: Boolean,
    isSaving: Boolean,
    onValueChange: (HabitDetail) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Create New Habit",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            InputForm(
                habitDetail = habitDetail,
                onValueChange = onValueChange
            )
        }
        Button(
            onClick = onSaveClick,
            enabled = isEntryValid && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if(isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Save Habit",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun InputForm(
    habitDetail: HabitDetail,
    onValueChange: (HabitDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth().padding(16.dp)
    ) {
        OutlinedTextField(
            value = habitDetail.name,
            onValueChange = { onValueChange(habitDetail.copy(name = it)) },
            label = { Text("Habit Name") },
            placeholder = { Text("E.g. Drink Water") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = ""
                )
            },
            supportingText = {
                if (habitDetail.name.isEmpty()) "Required*" else "Enter the name of your habit"
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = habitDetail.description,
            onValueChange = { onValueChange(habitDetail.copy(description = it)) },
            label = { Text("Description") },
            placeholder = { Text("Why do you want to do this") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = habitDetail.frequency,
            onValueChange = { onValueChange(habitDetail.copy(frequency = it)) },
            label = { Text("Daily Frequency") },
            placeholder = { Text("1") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Numbers,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
