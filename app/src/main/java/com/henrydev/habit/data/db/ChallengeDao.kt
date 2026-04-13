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

    @Query("SELECT linkedHabitId FROM challenge_subscriptions WHERE challengeId = :challengeId LIMIT 1")
    fun getLinkedHabitId(challengeId: Long): Flow<Long?>

    @Query("SELECT startDate FROM challenge_subscriptions WHERE challengeId = :challengeId LIMIT 1")
    fun getSubscriptionStartDate(challengeId: Long): Flow<Long?>

    @Query("SELECT * FROM challenge_subscriptions WHERE status = 'ACTIVE' ")
    fun getActiveSubscriptions(): Flow<List<ChallengeSubscriptionEntity>>

    @Query("SELECT * FROM challenge_subscriptions WHERE linkedHabitId = :habitId AND status = 'ACTIVE' lIMIT 1")
    suspend fun getActiveSubscriptionByHabit(habitId: Long): ChallengeSubscriptionEntity?

    @Query("UPDATE challenge_subscriptions SET isXpAwarded = 1, status = 'COMPLETED' WHERE subscriptionId = :subscriptionId")
    suspend fun markChallengeAsCompletedAndAwarded(subscriptionId: Long)

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    suspend fun getChallengeById(challengeId: Long): ChallengeEntity

}