package com.henrydev.habit.domain.use_cases

import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailableChallengesUseCase @Inject constructor(
    private val repository: ChallengeRepository
) {
    operator fun invoke(): Flow<List<Challenge>> {
        return repository.getAllChallenges()
    }
}