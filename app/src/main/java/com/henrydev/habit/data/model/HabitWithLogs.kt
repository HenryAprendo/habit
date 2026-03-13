package com.henrydev.habit.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity

data class HabitWithLogs(
    @Embedded
    val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val logs: List<HabitLogEntity>
)
