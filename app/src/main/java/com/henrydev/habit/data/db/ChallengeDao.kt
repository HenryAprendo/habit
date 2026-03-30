package com.henrydev.habit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.henrydev.habit.data.entities.ChallengeEntity
import com.henrydev.habit.data.entities.ChallengeSubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Query("SELECT * FROM challenges ORDER BY createdAt DESC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("""
        SELECT * FROM challenges
        INNER JOIN challenge_subscriptions ON challenges.id = challenge_subscriptions.challengeId
        WHERE challenge_subscriptions.status = 'ACTIVE'
    """)
    fun getActiveChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun subscribeToChallenge(subscription: ChallengeSubscriptionEntity)

    @Update
    suspend fun updateSubscriptionStatus(subscription: ChallengeSubscriptionEntity)

    @Query("DELETE FROM challenge_subscriptions WHERE challengeId = :challengeId")
    suspend fun deleteSubscription(challengeId: Long)

    @Query("SELECT * FROM challenges LIMIT 1")
    suspend fun getAnyChallenge(): ChallengeEntity?

}