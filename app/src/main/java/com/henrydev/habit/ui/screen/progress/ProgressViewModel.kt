package com.henrydev.habit.ui.screen.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.HabitProgressDetail
import com.henrydev.habit.domain.subscription.model.HabitStats
import com.henrydev.habit.domain.subscription.model.UserStatus
import com.henrydev.habit.domain.subscription.repository.SubscriptionRepository
import com.henrydev.habit.domain.subscription.usecase.GetGlobalStatsUseCase
import com.henrydev.habit.domain.subscription.usecase.GetHabitsProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val getGlobalStatsUseCase: GetGlobalStatsUseCase,
    private val getHabitsProgressUseCase: GetHabitsProgressUseCase,
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {

    val uiState: StateFlow<ProgressUiState> = combine(
        getGlobalStatsUseCase(),
        getHabitsProgressUseCase(),
        subscriptionRepository.getUserStatus()
    ) { stats, habitsProgress, userStatus ->

        val isPro = userStatus is UserStatus.Pro
        if (stats.heatmapData.isEmpty() && stats.totalCompletionRate == 0f && habitsProgress.isEmpty()) {
            ProgressUiState.Empty
        } else {
            ProgressUiState.Success(
                stats = stats,
                habitsProgress = habitsProgress,
                isPro = isPro
            )
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState.Loading
     )

}

sealed interface ProgressUiState {
    data object Loading: ProgressUiState
    data object Empty: ProgressUiState
    data class Success(
        val stats: HabitStats,
        val habitsProgress: List<HabitProgressDetail>,
        val isPro: Boolean
    ): ProgressUiState
}