package com.henrydev.habit.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Agrega un nuevo habito",
            style = MaterialTheme.typography.titleLarge
        )
        InputForm(
            habitDetail = uiState.habitDetail,
            onChangeForm = { habitDetail -> viewModel.onChangeForm(habitDetail) }
        )
        Button(onClick = { viewModel.insertHabit() }) {
            Text("Guardar")
        }
    }
}

@Composable
fun InputForm(
    habitDetail: HabitDetail,
    onChangeForm: (HabitDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = habitDetail.name,
            onValueChange = { onChangeForm(habitDetail.copy(name = it)) },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = habitDetail.description,
            onValueChange = { onChangeForm(habitDetail.copy(description = it)) },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = habitDetail.frequency,
            onValueChange = { onChangeForm(habitDetail.copy(frequency = it)) },
            label = { Text("Frequency") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
