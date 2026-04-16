package com.henrydev.faithsteward.domain.model

/**
 * Represents the performance metrics for a specific habit
 */
data class HabitProgressDetail(
    val habitId: Long,
    val habitName: String,
    val completionRate: Float,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalCompletions: Int,
    val lastSevenDays: List<DayStatus>
)

data class DayStatus(
    val date: Long,
    val isCompleted: Boolean
)