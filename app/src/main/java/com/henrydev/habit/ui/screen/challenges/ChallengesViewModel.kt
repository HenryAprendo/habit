package com.henrydev.habit.ui.screen.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.use_cases.GetAvailableChallengesUseCase
import com.henrydev.habit.domain.use_cases.JoinChallengeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val getAvailableChallengesUseCase: GetAvailableChallengesUseCase,
    private val joinChallengeUseCase: JoinChallengeUseCase,
    private val habitRepository: HabitRepository
): ViewModel() {

    private val _joinStatus = MutableStateFlow<JoinChallengeStatus>(JoinChallengeStatus.Idle)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val availableHabits = habitRepository.getAllHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<ChallengesUiState> = combine(
        getAvailableChallengesUseCase(),
        _joinStatus,
        _errorMessage
    ) { challenges, joinStatus, error ->
        ChallengesUiState(
            isLoading = false,
            challenges = challenges,
            joinStatus = joinStatus,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChallengesUiState()
    )

    fun joinChallenge(challenge: Challenge, linkedHabitId: Long) {
        viewModelScope.launch {
            _joinStatus.value = JoinChallengeStatus.Loading
            val result = joinChallengeUseCase(challenge, linkedHabitId)
            result.onSuccess {
                _joinStatus.value = JoinChallengeStatus.Success
            }.onFailure { exception ->
                if (exception.message?.contains("Pro") == true) {
                    _joinStatus.value = JoinChallengeStatus.ProRequired
                } else {
                    _joinStatus.value = JoinChallengeStatus.Error(
                        exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun resetJoinStatus() {
        _joinStatus.value = JoinChallengeStatus.Idle
    }

}

sealed interface JoinChallengeStatus {
    data object Idle: JoinChallengeStatus
    data object Loading: JoinChallengeStatus
    data object Success: JoinChallengeStatus
    data object ProRequired: JoinChallengeStatus
    data class Error(val message: String): JoinChallengeStatus
}

data class ChallengesUiState(
    val isLoading: Boolean = true,
    val challenges: List<Challenge> = emptyList(),
    val joinStatus: JoinChallengeStatus = JoinChallengeStatus.Idle,
    val errorMessage: String? = null
)