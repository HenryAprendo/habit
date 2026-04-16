package com.henrydev.faithsteward.domain.use_cases

import com.henrydev.faithsteward.domain.model.Challenge
import com.henrydev.faithsteward.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailableChallengesUseCase @Inject constructor(
    private val repository: ChallengeRepository
) {
    operator fun invoke(): Flow<List<Challenge>> {
        return repository.getAllChallenges()
    }
}