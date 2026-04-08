package com.henrydev.habit.data.db

import com.henrydev.habit.data.entities.HabitEntity

object HabitSeeds {
    // List of default spiritual disciplines based strictly on HabitEntity
    val DEFAULT_SPIRITUAL_HABITs = listOf(
        HabitEntity(
            id = 0,
            name = "Daily Scripture Reading",
            description = "Spend time reading and meditating on God's Word.",
            frequency = 1, // Daily
            createdAt = System.currentTimeMillis()
        ),
        HabitEntity(
            id = 0,
            name = "Morning Prayer",
            description = "Dedicate the first moments of your day to talk with God.",
            frequency = 1,
            createdAt = System.currentTimeMillis()
        ),
        HabitEntity(
            id = 0,
            name = "Evening Gratitude",
            description = "Reflect on today's blessings and give thanks to the Lord.",
            frequency = 1,
            createdAt = System.currentTimeMillis()
        ),
        HabitEntity(
            id = 0,
            name = "Weekly Fasting",
            description = "A day of spiritual sacrifice to seek God's presence.",
            frequency = 7, // Weekly
            createdAt = System.currentTimeMillis()
        )
    )
}