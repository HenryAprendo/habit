package com.henrydev.habit.domain.model

data class HabitWithHistory(
    val habit: Habit,
    val history: List<HabitLog>
)
