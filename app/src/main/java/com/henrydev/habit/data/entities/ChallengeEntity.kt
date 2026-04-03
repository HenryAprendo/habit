package com.henrydev.habit.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.henrydev.habit.domain.model.ChallengeStatus

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val durationDays: Int,
    val isPro: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "challenge_subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = ChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["linkedHabitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["challengeId"]),
        Index(value = ["linkedHabitId"])
    ]
)
data class  ChallengeSubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val subscriptionId: Long = 0,
    val challengeId: Long,
    val linkedHabitId: Long,
    val startDate: Long,
    val status: ChallengeStatus
)


