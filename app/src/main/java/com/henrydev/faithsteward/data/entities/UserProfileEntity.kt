package com.henrydev.faithsteward.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalXp: Long = 0,
    val level: Int = 1,
    val displayName: String = "Habit Hero"
)