package com.henrydev.habit.ui.home

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.use_cases.GetHabitsWithHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val getHabitsWithHistoryUseCase: GetHabitsWithHistoryUseCase,
    private val habitRepository: HabitRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        getHabitsWithHistoryUseCase()
            .map { habitsWithHistory ->
                if (habitsWithHistory.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    val items = habitsWithHistory.map { item ->
                        val isCompletedToday = item.history.any { log ->
                            isToday(log.date) && log.isCompleted
                        }
                        HabitItemState(
                            habit = item.habit,
                            isCompleted = isCompletedToday
                        )
                    }
                    HomeUiState.Success(habits = items)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState.Loading
            )

    fun toggleHabit(habitId:Int, currentStatus: Boolean) {
        viewModelScope.launch {
            habitRepository.toggleHabitCompletion(
                habitId = habitId,
                date = System.currentTimeMillis(),
                isCompleted = !currentStatus
            )
        }
    }


    private fun isToday(dateMillis: Long): Boolean {
        val today = Calendar.getInstance()
        val logDate = Calendar.getInstance().apply {
            timeInMillis = dateMillis
        }
        return today.get(Calendar.YEAR) == logDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == logDate.get(Calendar.DAY_OF_YEAR)
    }

}

data class HabitItemState(
    val habit: Habit,
    val isCompleted: Boolean
)

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(val habits: List<HabitItemState>) : HomeUiState
}