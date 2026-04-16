package com.henrydev.faithsteward.data.db

import com.henrydev.faithsteward.data.entities.ChallengeEntity

object ChallengeSeeds {
    val DEFAULT_CHALLENGES = listOf(
        // --- FREE CHALLENGES (Beginner & Intermediate) ---

        // ID 1: 3 Days of Gratitude
        ChallengeEntity(
            id = 1,
            title = "ch_gratitude_3_title",
            description = "ch_gratitude_3_desc",
            category = "cat_spiritual",
            durationDays = 3,
            isPro = false
        ),
        // ID 2: 7 Days Morning Prayer
        ChallengeEntity(
            id = 2,
            title = "ch_prayer_7_title",
            description = "ch_prayer_7_desc",
            category = "cat_prayer",
            durationDays = 7,
            isPro = false
        ),
        // ID 3: 7 Days Praise in Storm
        ChallengeEntity(
            id = 3,
            title = "ch_praise_7_title",
            description = "ch_praise_7_desc",
            category = "cat_praise",
            durationDays = 7,
            isPro = false
        ),
        // ID 4: 10 Days Proverbs Wisdom
        ChallengeEntity(
            id = 4,
            title = "ch_proverbs_10_title",
            description = "ch_proverbs_10_desc",
            category = "cat_bible",
            durationDays = 10,
            isPro = false
        ),
        // ID 5: 15 Days Soul Rest
        ChallengeEntity(
            id = 5,
            title = "ch_rest_15_title",
            description = "ch_rest_15_desc",
            category = "cat_discipline",
            durationDays = 15,
            isPro = false
        ),
        // ID 6: 7 Days Armor of God
        ChallengeEntity(
            id = 6,
            title = "ch_armor_7_title",
            description = "ch_armor_7_desc",
            category = "cat_spiritual",
            durationDays = 7,
            isPro = false
        ),

        // --- PRO CHALLENGES (Advanced Consecration) ---

        // ID 7: 15 Days Intercession Warrior
        ChallengeEntity(
            id = 7,
            title = "ch_intercession_15_title",
            description = "ch_intercession_15_desc",
            category = "cat_prayer",
            durationDays = 15,
            isPro = true
        ),
        // ID 8: 21 Days Daniel's Discipline
        ChallengeEntity(
            id = 8,
            title = "ch_daniel_21_title",
            description = "ch_daniel_21_desc",
            category = "cat_discipline",
            durationDays = 21,
            isPro = true
        ),
        // ID 9: 30 Days New Testament Master
        ChallengeEntity(
            id = 9,
            title = "ch_nt_master_30_title",
            description = "ch_nt_master_30_desc",
            category = "cat_bible",
            durationDays = 30,
            isPro = true
        ),
        // ID 10: 21 Days Heart of Worship
        ChallengeEntity(
            id = 10,
            title = "ch_worship_21_title",
            description = "ch_worship_21_desc",
            category = "cat_praise",
            durationDays = 21,
            isPro = true
        ),
        // ID 11: 30 Days Faith Over Fear
        ChallengeEntity(
            id = 11,
            title = "ch_faith_30_title",
            description = "ch_faith_30_desc",
            category = "cat_spiritual",
            durationDays = 30,
            isPro = true
        ),
        // ID 12: 15 Days Biblical Stewardship
        ChallengeEntity(
            id = 12,
            title = "ch_steward_15_title",
            description = "ch_steward_15_desc",
            category = "cat_discipline",
            durationDays = 15,
            isPro = true
        )
    )
}
