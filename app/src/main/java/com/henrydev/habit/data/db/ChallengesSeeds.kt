package com.henrydev.habit.data.db

import com.henrydev.habit.data.entities.ChallengeEntity

/**
 * Spiritual Discipline Challenges for the Christian niche.
 * These seeds provide immediate value upon first install.
 */
object ChallengeSeeds {
    val DEFAULT_CHALLENGES = listOf(
        // FREE CHALLENGES
        ChallengeEntity(
            id = 1,
            title = "First 7 Days of Prayer",
            description = "Establish a solid foundation by talking to God every morning for a week.",
            category = "Spiritual",
            durationDays = 2,
            isPro = false
        ),
        ChallengeEntity(
            id = 2,
            title = "Proverbs Wisdom Journey",
            description = "Read one chapter of Proverbs daily for 15 days to gain divine wisdom.",
            category = "Spiritual",
            durationDays = 15,
            isPro = false
        ),
        ChallengeEntity(
            id = 3,
            title = "The Gratitude Shield",
            description = "Shield your heart by reflecting on God's blessings every night for 10 days.",
            category = "Spiritual",
            durationDays = 10,
            isPro = false
        ),

        // PRO CHALLENGES
        ChallengeEntity(
            id = 4,
            title = "Daniel's Fasting Discipline",
            description = "A 21-day advanced challenge of fasting and consecration for spiritual breakthrough.",
            category = "Discipline",
            durationDays = 21,
            isPro = true
        ),
        ChallengeEntity(
            id = 5,
            title = "Master of the New Testament",
            description = "An intensive 30-day program to read through the entire New Testament.",
            category = "Discipline",
            durationDays = 30,
            isPro = true
        )
    )
}