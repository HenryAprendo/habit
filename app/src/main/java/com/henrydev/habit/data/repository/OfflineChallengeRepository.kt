package com.henrydev.habit.data.repository

import com.henrydev.habit.data.db.ChallengeDao
import com.henrydev.habit.data.entities.ChallengeSubscriptionEntity
import com.henrydev.habit.data.mapper.toDomain
import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.model.ChallengeStatus
import com.henrydev.habit.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineChallengeRepository @Inject constructor (
    private val challengeDao:  ChallengeDao
) : ChallengeRepository {

    override fun getAllChallenges(): Flow<List<Challenge>> {
        return challengeDao.getAllChallenges().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveChallenges(): Flow<List<Challenge>> {
        return challengeDao.getActiveChallenges().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun subscribeToChallenge(challengeId: Long, linkedHabitId: Long) {
        val subscription = ChallengeSubscriptionEntity(
            challengeId = challengeId,
            linkedHabitId = linkedHabitId,
            startDate = System.currentTimeMillis(),
            status = ChallengeStatus.ACTIVE,

        )
        challengeDao.subscribeToChallenge(subscription)
    }

    override suspend fun updateChallengesStatus(
        challengeId: Long,
        status: ChallengeStatus,
        linkedHabitId: Long
    ) {
        val subscription = ChallengeSubscriptionEntity(
            challengeId = challengeId,
            linkedHabitId = linkedHabitId,
            startDate = System.currentTimeMillis(),
            status = status
        )
        challengeDao.updateSubscriptionStatus(subscription)
    }

}