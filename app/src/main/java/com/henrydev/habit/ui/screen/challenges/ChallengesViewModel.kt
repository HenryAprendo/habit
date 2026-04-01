package com.henrydev.habit.ui.screen.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.model.ChallengeProgress
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.repository.ChallengeRepository
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.use_cases.GetAvailableChallengesUseCase
import com.henrydev.habit.domain.use_cases.GetChallengeProgressUseCase
import com.henrydev.habit.domain.use_cases.JoinChallengeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val getAvailableChallengesUseCase: GetAvailableChallengesUseCase,
    private val joinChallengeUseCase: JoinChallengeUseCase,
    private val getChallengeProgressUseCase: GetChallengeProgressUseCase,
    private val habitRepository: HabitRepository,
    private val challengeRepository: ChallengeRepository
): ViewModel() {

    private val _joinStatus = MutableStateFlow<JoinChallengeStatus>(JoinChallengeStatus.Idle)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val availableHabits: StateFlow<List<Habit>> = habitRepository.getAllHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ChallengesUiState> = combine(
        getAvailableChallengesUseCase(),
        _joinStatus,
        _errorMessage,
    ) { challenges, joinStatus, error ->
        Triple(challenges,joinStatus, error)
    }.flatMapLatest { (challenges, joinStatus, error) ->
        if (challenges.isEmpty()) {
            flowOf(
                ChallengesUiState(
                    isLoading = false,
                    challenges = emptyList(),
                    joinStatus = joinStatus,
                    errorMessage = error
                )
            )
        } else {
            // Create a list of progress flows for each challenge
            val progressFlows = challenges.map { challenge ->
                combine(
                    challengeRepository.getLinkedHabitId(challenge.id),
                    challengeRepository.getSubscriptionStartDate(challenge.id)
                ) { linkedId, startDate ->
                    Pair(linkedId,startDate)
                }.flatMapLatest{ (linkedId, startDate) ->
                    if (linkedId != null && startDate != null) {
                        getChallengeProgressUseCase(
                            challenge = challenge,
                            linkedHabitId = linkedId,
                            startDate
                        )
                    } else {
                        flowOf(
                            ChallengeProgress(
                                challengeId = challenge.id,
                                linkedHabitId = 0L,
                                currentStreak = 0,
                                completedDays = 0,
                                totalDays = challenge.durationDays,
                                progressPercentage = 0f,
                                daysRemaining = challenge.durationDays,
                                isCompleted = false
                            )
                        )
                    }
                }
            }

            combine(progressFlows) { progressArray ->
                ChallengesUiState(
                    isLoading = false,
                    challenges = challenges,
                    progressMap = progressArray.associateBy { it.challengeId },
                    joinStatus = joinStatus,
                    errorMessage = error
                )
            }
        }

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
    val progressMap: Map<Long, ChallengeProgress> = emptyMap(),
    val joinStatus: JoinChallengeStatus = JoinChallengeStatus.Idle,
    val errorMessage: String? = null
)