package com.henrydev.habit.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            InputForm(
                habitDetail = uiState.habitDetail,
                onChangeForm = { habitDetail -> viewModel.onChangeForm(habitDetail) }
            )
            Button(onClick = {
                viewModel.insertHabit()
                onNavigateBack()
            }) {
                Text("Guardar")
            }
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
