package com.henrydev.faithsteward.domain.model

data class Challenge(
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val durationDays: Int,
    val isPro: Boolean
)
