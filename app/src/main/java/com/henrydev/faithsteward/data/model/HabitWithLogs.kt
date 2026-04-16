package com.henrydev.faithsteward.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.henrydev.faithsteward.data.entities.HabitEntity
import com.henrydev.faithsteward.data.entities.HabitLogEntity

data class HabitWithLogs(
    @Embedded
    val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val logs: List<HabitLogEntity>
)
