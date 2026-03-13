package com.henrydev.habit.data.mapper

import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity
import com.henrydev.habit.data.model.HabitWithLogs
import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.model.HabitLog
import com.henrydev.habit.domain.model.HabitWithHistory

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = this.id,
        name = this.name,
        description = this.description,
        frequency = this.frequency,
        createdAt = this.createdAt
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        frequency = this.frequency,
        createdAt = this.createdAt
    )
}

fun HabitLogEntity.toDomain(): HabitLog {
    return HabitLog(
        date = this.date,
        isCompleted = this.isCompleted
    )
}

fun HabitWithLogs.toDomain(): HabitWithHistory {
    return HabitWithHistory(
        habit = this.habit.toDomain(),
        history = this.logs.map { it.toDomain() }
    )
}







