package com.henrydev.habit.data.repository

import com.henrydev.habit.data.db.HabitDao
import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity
import com.henrydev.habit.data.mapper.toDomain
import com.henrydev.habit.data.mapper.toEntity
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.model.HabitLog
import com.henrydev.habit.domain.model.HabitWithHistory
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.util.toStartOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineHabitRepository @Inject constructor(
    private val habitDao: HabitDao
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
    ) {
        val normalizedDate = date.toStartOfDay()
        val log = HabitLogEntity(
            logId = 0,
            habitId = habitId,
            date = normalizedDate,
            isCompleted = isCompleted
        )
        habitDao.insertLog(log)
    }

    override suspend fun restoreBackup(data: List<Pair<HabitEntity, List<HabitLogEntity>>>) {
        habitDao.replaceAllData(data)
    }

}