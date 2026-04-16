package com.henrydev.faithsteward.domain.model

data class HabitWithHistory(
    val habit: Habit,
    val history: List<HabitLog>
)
