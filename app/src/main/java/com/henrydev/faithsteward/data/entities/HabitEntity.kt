package com.henrydev.faithsteward.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits"
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val frequency: Int,
    val createdAt: Long
)
