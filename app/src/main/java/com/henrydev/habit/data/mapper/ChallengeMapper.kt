package com.henrydev.habit.data.mapper

import com.henrydev.habit.data.entities.ChallengeEntity
import com.henrydev.habit.domain.model.Challenge

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