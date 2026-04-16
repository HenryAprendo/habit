package com.henrydev.faithsteward.data.repository

import com.henrydev.faithsteward.data.db.ChallengeDao
import com.henrydev.faithsteward.data.db.HabitDao
import com.henrydev.faithsteward.data.entities.HabitEntity
import com.henrydev.faithsteward.data.entities.HabitLogEntity
import com.henrydev.faithsteward.data.mapper.toDomain
import com.henrydev.faithsteward.data.mapper.toEntity
import com.henrydev.faithsteward.domain.model.ChallengeStatus
import com.henrydev.faithsteward.domain.model.Habit
import com.henrydev.faithsteward.domain.model.HabitWithHistory
import com.henrydev.faithsteward.domain.repository.HabitRepository
import com.henrydev.faithsteward.domain.util.toStartOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineHabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val challengeDao: ChallengeDao
): HabitRepository {

    override fun getHabitsWithHistory(): Flow<List<HabitWithHistory>> {
        return habitDao.getHabitsWithLogs().map { habitWithLogs ->
            habitWithLogs.map{ item -> item.toDomain() }
        }
    }

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    override suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun toggleHabitCompletion(
        habitId: Long,
        date: Long,
        isCompleted: Boolean
    ): ChallengeStatus {
        val normalizedDate = date.toStartOfDay()
        val log = HabitLogEntity(
            logId = 0,
            habitId = habitId,
            date = normalizedDate,
            isCompleted = isCompleted
        )
        habitDao.insertLog(log)
        val subscription = challengeDao.getActiveSubscriptionByHabit(habitId)
        if (subscription != null && !subscription.isXpAwarded) {
            val challenge = challengeDao.getChallengeById(subscription.challengeId)
            val logsCount = habitDao.getLogsCounterAfter(habitId,subscription.startDate)

            if (logsCount >= challenge.durationDays) {
                challengeDao.markChallengeAsCompletedAndAwarded(subscription.subscriptionId)
                return ChallengeStatus.COMPLETED
            }
        }
        return ChallengeStatus.ACTIVE
    }

    override suspend fun restoreBackup(data: List<Pair<HabitEntity, List<HabitLogEntity>>>) {
        habitDao.replaceAllData(data)
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit.toEntity())
    }

    override suspend fun getHabitById(id: Long): Habit? {
        return habitDao.getHabitById(id)?.toDomain()
    }

}