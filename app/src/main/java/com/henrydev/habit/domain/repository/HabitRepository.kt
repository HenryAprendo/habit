package com.henrydev.habit.domain.repository

import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.model.HabitLog
import com.henrydev.habit.domain.model.HabitWithHistory
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabitsWithHistory(): Flow<List<HabitWithHistory>>
    fun getAllHabits(): Flow<List<Habit>>
    suspend fun insertHabit(habit: Habit)
    suspend fun toggleHabitCompletion(habitId: Int, date: Long, isCompleted: Boolean)
}