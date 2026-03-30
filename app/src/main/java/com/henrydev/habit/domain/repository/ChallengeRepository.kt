package com.henrydev.habit.domain.repository

import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.model.ChallengeStatus
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun getAllChallenges(): Flow<List<Challenge>>
    fun getActiveChallenges(): Flow<List<Challenge>>
    suspend fun subscribeToChallenge(challengeId: Long,linkedHabitId: Long)
    suspend fun updateChallengesStatus(challengeId: Long, status: ChallengeStatus, linkedHabitId: Long)
}