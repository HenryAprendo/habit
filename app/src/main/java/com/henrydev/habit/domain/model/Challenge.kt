package com.henrydev.habit.domain.model

data class Challenge(
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val durationDays: Int,
    val isPro: Boolean
)
