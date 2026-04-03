package com.henrydev.habit.domain.repository

import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.model.HabitLog
import com.henrydev.habit.domain.model.HabitWithHistory
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabitsWithHistory(): Flow<List<HabitWithHistory>>
    fun getAllHabits(): Flow<List<Habit>>
    suspend fun insertHabit(habit: Habit): Long
    suspend fun toggleHabitCompletion(habitId: Long, date: Long, isCompleted: Boolean)
    suspend fun restoreBackup(data: List<Pair<HabitEntity, List<HabitLogEntity>>>)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun getHabitById(id: Long): Habit?
}