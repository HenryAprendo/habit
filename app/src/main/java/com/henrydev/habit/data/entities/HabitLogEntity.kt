package com.henrydev.habit.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId","date"], unique = true)]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,
    val habitId: Int,
    val date: Long,
    val isCompleted: Boolean
)