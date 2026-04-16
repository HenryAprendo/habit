package com.henrydev.faithsteward.data.mapper

import com.henrydev.faithsteward.data.entities.ChallengeEntity
import com.henrydev.faithsteward.data.entities.ChallengeSubscriptionEntity
import com.henrydev.faithsteward.domain.model.Challenge
import com.henrydev.faithsteward.domain.model.ChallengeSubscription

fun ChallengeEntity.toDomain(): Challenge {
    return Challenge(
        id = id,
        title = title,
        description = description,
        category = category,
        durationDays = durationDays,
        isPro = isPro
    )
}

fun Challenge.toEntity(): ChallengeEntity {
    return ChallengeEntity(
        id = id,
        title = title,
        description = description,
        category = category,
        durationDays = durationDays,
        isPro = isPro
    )
}

fun ChallengeSubscriptionEntity.toDomain(): ChallengeSubscription {
    return ChallengeSubscription(
        subscriptionId = subscriptionId,
        challengeId = challengeId,
        linkedHabitId = linkedHabitId,
        startDate = startDate,
        status = status
    )
}













