package com.henrydev.habit.ui.screen.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.ChallengeStatus
import com.henrydev.habit.domain.model.DailyDevotional
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.model.UserStats
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.subscription.usecase.EarnXpUseCase
import com.henrydev.habit.domain.subscription.usecase.IsProUserUseCase
import com.henrydev.habit.domain.use_cases.DeleteHabitUseCase
import com.henrydev.habit.domain.use_cases.GetHomeHabitUseCase
import com.henrydev.habit.domain.use_cases.GetUserLevelUseCase
import com.henrydev.habit.domain.use_cases.HabitItemState
import com.henrydev.habit.domain.use_cases.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val application: Application,
    getHomeHabitUseCase: GetHomeHabitUseCase,
    isProUserUseCase: IsProUserUseCase,
    private val habitRepository: HabitRepository,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val getUserLevelUseCase: GetUserLevelUseCase,
    private val earnXpUseCase: EarnXpUseCase,
) : ViewModel() {

    val dailyDevotional: StateFlow<DailyDevotional?> = flow {
        val devotional = calculateDailyDevotional()
        emit(devotional)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private fun calculateDailyDevotional(): DailyDevotional {
        val res = application.resources
        val packageName = application.packageName
        val dayOfYear = LocalDate.now().dayOfYear

        // We have 15 days of content, so we use modulo to cycle them
        // day 1-15 -> 1-15, day 16 -> 1, day 17 -> 2...
        val dayIndex = ((dayOfYear-1) % 15) + 1

        val verseId = res.getIdentifier("devotional_verse_$dayIndex", "string", packageName)
        val refId = res.getIdentifier("devotional_ref_$dayIndex", "string", packageName)
        val reflectedId = res.getIdentifier("devotional_reflect_$dayIndex", "string", packageName)

        return DailyDevotional(
            verse = res.getString(verseId),
            reference = res.getString(refId),
            reflection = res.getString(reflectedId)
        )

    }

    private val _actionState = MutableStateFlow(HabitActionState())
    val actionState = _actionState.asStateFlow()

    val userStats: StateFlow<UserStats?> = getUserLevelUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onHabitLongClick(habit: Habit) {
        _actionState.update { it.copy( selectedHabit = habit, showActionSheet = true) }
    }

    fun openEditDialog() {
        _actionState.update { it.copy(showActionSheet = false, showEditDialog = true) }
    }

    fun  openDeleteConfirmation() {
        _actionState.update { it.copy(showActionSheet = false, showDeleteConfirmation = true) }
    }

    fun dismissAllActions() {
        _actionState.update { HabitActionState() }
    }

    fun deleteHabit() {
        val habitToDelete = _actionState.value.selectedHabit ?: return
        viewModelScope.launch {
            deleteHabitUseCase(habitToDelete)
            dismissAllActions()
        }
    }

    fun updateHabit(newName: String, newDescription: String) {
        val habitToUpdate = _actionState.value.selectedHabit ?: return
        viewModelScope.launch {
            val updatedHabit = habitToUpdate.copy(
                name = newName,
                description = newDescription
            )
            updateHabitUseCase(updatedHabit)
            dismissAllActions()
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        getHomeHabitUseCase(),
        isProUserUseCase()
    ) { habits, isPro ->
        if (habits.isEmpty()) {
            HomeUiState.Empty
        } else {
            HomeUiState.Success(habits = habits, showAds = !isPro)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun toggleHabit(habitId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is HomeUiState.Success) {
                val habitItem = currentState.habits.find { it.habit.id == habitId }
                val currentStreak = habitItem?.streakCounter ?: 0

                val missionStatus = habitRepository.toggleHabitCompletion(
                    habitId = habitId,
                    date = System.currentTimeMillis(),
                    isCompleted = !currentStatus
                )
                if (!currentStatus) {
                    earnXpUseCase.onHabitCompleted(currentStreak = currentStreak)
                    if (missionStatus == ChallengeStatus.COMPLETED) earnXpUseCase.onChallengeCompleted()

                } else {
                    earnXpUseCase.onHabitUnchecked()
                }
            }
        }
    }

}

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(
        val habits: List<HabitItemState>,
        val showAds: Boolean
    ) : HomeUiState
}

data class HabitActionState(
    val selectedHabit: Habit? = null,
    val showActionSheet: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteConfirmation: Boolean = false
)