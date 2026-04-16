package com.henrydev.faithsteward.data.mapper

import com.henrydev.faithsteward.data.entities.HabitEntity
import com.henrydev.faithsteward.data.entities.HabitLogEntity
import com.henrydev.faithsteward.data.model.HabitWithLogs
import com.henrydev.faithsteward.domain.model.Habit
import com.henrydev.faithsteward.domain.model.HabitLog
import com.henrydev.faithsteward.domain.model.HabitWithHistory

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







