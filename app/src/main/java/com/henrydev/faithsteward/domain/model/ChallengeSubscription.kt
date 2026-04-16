package com.henrydev.faithsteward.domain.model

data class ChallengeSubscription(
    val subscriptionId: Long = 0,
    val challengeId: Long,
    val linkedHabitId: Long,
    val startDate: Long,
    val status: ChallengeStatus
)