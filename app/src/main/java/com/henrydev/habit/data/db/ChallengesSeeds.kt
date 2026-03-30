package com.henrydev.habit.data.db

import com.henrydev.habit.data.entities.ChallengeEntity


object ChallengeSeeds {
    val DEFAULT_CHALLENGES = listOf(
        ChallengeEntity(
            id = 1,
            title = "Hydration Week",
            description = "Drink at least 2 liters of water every day to stay healthy.",
            category = "Health",
            durationDays = 7,
            isPro = false
        ),
        ChallengeEntity(
            id = 2,
            title = "30-Day Mindfulness",
            description = "Spend 10 minutes a day meditating to reduce stress and improve focus.",
            category = "Wellness",
            durationDays = 30,
            isPro = true
        ),
        ChallengeEntity(
            id = 3,
            title = "Consistent Reading",
            description = "Read at least 15 pages of a book every single day.",
            category = "Productivity",
            durationDays = 21,
            isPro = false
        )
    )
}