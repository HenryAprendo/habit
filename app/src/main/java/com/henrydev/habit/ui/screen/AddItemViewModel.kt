package com.henrydev.habit.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val habitRepository: HabitRepository
): ViewModel() {

    private val _uiState: MutableStateFlow<AddHabitUiState> = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    fun onChangeForm(input: HabitDetail) {
        _uiState.update {
            it.copy(habitDetail = input)
        }
    }

    fun insertHabit() {
        viewModelScope.launch {
            if (validate()) {
                habitRepository.insertHabit(uiState.value.habitDetail.toHabit())
            }
        }
    }

    fun validate(uiState: HabitDetail = _uiState.value.habitDetail): Boolean {
        with(uiState){
            return name.isNotBlank() &&
                    description.isNotBlank() &&
                    frequency.isNotBlank() &&
                    (frequency.toIntOrNull() ?: 0) > 0
        }
    }

}


data class AddHabitUiState(
    val habitDetail: HabitDetail = HabitDetail()
)

data class HabitDetail(
    val name: String = "",
    val description: String = "",
    val frequency: String = "",
)

fun HabitDetail.toHabit(): Habit = Habit(
    id = 0,
    name = this.name,
    description = this.description,
    frequency = this.frequency.toIntOrNull() ?: 1,
    createdAt = System.currentTimeMillis()
)