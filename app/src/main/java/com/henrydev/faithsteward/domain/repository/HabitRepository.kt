package com.henrydev.faithsteward.domain.repository

import com.henrydev.faithsteward.data.entities.HabitEntity
import com.henrydev.faithsteward.data.entities.HabitLogEntity
import com.henrydev.faithsteward.domain.model.ChallengeStatus
import com.henrydev.faithsteward.domain.model.Habit
import com.henrydev.faithsteward.domain.model.HabitWithHistory
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabitsWithHistory(): Flow<List<HabitWithHistory>>
    fun getAllHabits(): Flow<List<Habit>>
    suspend fun insertHabit(habit: Habit): Long
    suspend fun toggleHabitCompletion(habitId: Long, date: Long, isCompleted: Boolean): ChallengeStatus
    suspend fun restoreBackup(data: List<Pair<HabitEntity, List<HabitLogEntity>>>)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun getHabitById(id: Long): Habit?
}