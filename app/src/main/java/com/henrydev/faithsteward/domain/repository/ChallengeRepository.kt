package com.henrydev.faithsteward.domain.repository

import com.henrydev.faithsteward.domain.model.Challenge
import com.henrydev.faithsteward.domain.model.ChallengeStatus
import com.henrydev.faithsteward.domain.model.ChallengeSubscription
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun getAllChallenges(): Flow<List<Challenge>>
    fun getActiveChallenges(): Flow<List<Challenge>>
    suspend fun subscribeToChallenge(challengeId: Long,linkedHabitId: Long)
    suspend fun updateChallengesStatus(challengeId: Long, status: ChallengeStatus, linkedHabitId: Long)
    fun getLinkedHabitId(challengeId: Long): Flow<Long?>
    fun getSubscriptionStartDate(challengeId: Long): Flow<Long?>
    fun getActiveSubscriptions(): Flow<List<ChallengeSubscription>>
}