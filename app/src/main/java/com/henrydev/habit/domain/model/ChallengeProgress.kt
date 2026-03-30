package com.henrydev.habit.domain.model

data class ChallengeProgress(
    val challengeId: Long,
    val linkedHabitId: Long,
    val currentStreak: Int,
    val completedDays: Int,
    val totalDays: Int,
    val progressPercentage: Float,
    val daysRemaining: Int,
    val isCompleted: Boolean
)
