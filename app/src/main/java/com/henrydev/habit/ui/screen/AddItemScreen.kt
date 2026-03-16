package com.henrydev.habit.ui.screen

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.HabitTopAppBar
import com.henrydev.habit.ui.navigation.HabitScreen

@Composable
fun AddItemScreen(
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
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

    Scaffold(
        topBar = {
            HabitTopAppBar(
                title = HabitScreen.AddHabit.title,
                navigateUp = onNavigateUp,
                canNavigateBack = true
            )
        },
        modifier = modifier
    ) { innerPadding ->
        AddItemBody(
            habitDetail = uiState.habitDetail,
            isEntryValid = uiState.isEntryValid,
            isSaving = uiState.isSaving,
            onValueChange = { viewModel.onChangeForm(it) },
            onSaveClick = { viewModel.insertHabit() },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()

        )
    }
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
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InputForm(
            habitDetail = habitDetail,
            onValueChange = onValueChange
        )
        Button(
            onClick = onSaveClick,
            enabled = isEntryValid && !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Guardar habito")
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
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = habitDetail.name,
            onValueChange = { onValueChange(habitDetail.copy(name = it)) },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = habitDetail.description,
            onValueChange = { onValueChange(habitDetail.copy(description = it)) },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = habitDetail.frequency,
            onValueChange = { onValueChange(habitDetail.copy(frequency = it)) },
            label = { Text("Frequency") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
