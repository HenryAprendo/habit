package com.henrydev.habit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.use_cases.GetHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val getHabitsUseCase: GetHabitsUseCase
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        getHabitsUseCase()
            .map { habits ->
                HomeUiState.Success(habits)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIME_OUT_MILLIS),
                initialValue = HomeUiState.Loading
            )

    companion object {
        const val TIME_OUT_MILLIS = 5_000L
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val habits: List<Habit>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}