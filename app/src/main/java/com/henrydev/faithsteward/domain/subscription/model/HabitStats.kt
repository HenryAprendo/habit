package com.henrydev.faithsteward.domain.subscription.model

data class HabitStats(
    val totalCompletionRate: Float,
    val perfectDaysCount: Int,
    val bestStreakRecord: Int,
    val heatmapData: Map<Long,Boolean>
)