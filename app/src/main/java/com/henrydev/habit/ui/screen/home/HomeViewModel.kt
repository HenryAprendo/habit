package com.henrydev.habit.ui.screen.home

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.subscription.usecase.IsProUserUseCase
import com.henrydev.habit.domain.use_cases.GetHabitsWithHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    getHabitsWithHistoryUseCase: GetHabitsWithHistoryUseCase,
    isProUserUseCase: IsProUserUseCase,
    private val habitRepository: HabitRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        getHabitsWithHistoryUseCase(),
        isProUserUseCase()
    ) { habitsWithHistory, isPro ->
        if (habitsWithHistory.isEmpty()) {
            HomeUiState.Empty
        } else {
            val items = habitsWithHistory.map { item ->
                val isCompletedToday = item.history.any { log ->
                    isToday(log.date) && log.isCompleted
                }

                val lastSevenDays = (0..6).map { daysAgo ->
                    Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR,-daysAgo)
                        set(Calendar.HOUR_OF_DAY,0)
                        set(Calendar.MINUTE,0)
                        set(Calendar.SECOND,0)
                        set(Calendar.MILLISECOND,0)
                    }.timeInMillis
                }.reversed()

                val weeklyProgress = lastSevenDays.map { referenceDate ->
                    val isDayCompleted = item.history.any { log ->
                        isSameDay(log.date,referenceDate) && log.isCompleted
                    }
                    HabitDayState(
                        date = referenceDate,
                        isCompleted = isDayCompleted
                    )
                }

                val referenceDate = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY,0)
                    set(Calendar.MINUTE,0)
                    set(Calendar.SECOND,0)
                    set(Calendar.MILLISECOND,0)
                }
                val sortedHistory = item.history.sortedByDescending { it.date }
                var streakCounter = 0
                var checkDate = referenceDate.clone() as Calendar

                // 3. Special Case: If not completed today, check if it was completed yesterday
                // to decide if the streak is still "active"
                val completedToday = sortedHistory.any {
                    isSameDay(it.date, referenceDate.timeInMillis) && it.isCompleted
                }

                // If not done today, the streak is based on yesterday
                if (!completedToday) {
                    checkDate.add(Calendar.DAY_OF_YEAR,-1)
                }

                for (dayOffset in 0..item.history.size) {
                    val dateToFind = checkDate.timeInMillis
                    val logFound = sortedHistory.find { isSameDay(it.date,dateToFind) }
                    if (logFound != null && logFound.isCompleted) {
                        streakCounter++
                        checkDate.add(Calendar.DAY_OF_YEAR,-1)
                    } else {
                        break
                    }
                }

                HabitItemState(
                    habit = item.habit,
                    isCompleted = isCompletedToday,
                    weeklyProgress = weeklyProgress,
                    streakCounter = streakCounter
                )
            }
            HomeUiState.Success(habits = items, showAds = !isPro)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun toggleHabit(habitId: Long, currentStatus: Boolean) {
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

    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

}

data class HabitItemState(
    val habit: Habit,
    val isCompleted: Boolean,
    val weeklyProgress: List<HabitDayState>,
    val streakCounter: Int
)

data class HabitDayState(
    val date: Long,
    val isCompleted: Boolean
)

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(
        val habits: List<HabitItemState>,
        val showAds: Boolean
    ) : HomeUiState
}