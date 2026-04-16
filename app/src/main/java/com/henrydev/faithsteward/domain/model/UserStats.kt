package com.henrydev.faithsteward.domain.model

data class UserStats(
    val totalXp: Long,
    val level: Int,
    val rankTitle: String,
    val progressToNextLevel: Float,
    val xpRequiredForNext: Long
)
