package com.henrydev.faithsteward.domain.model


data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String,
    val frequency: Int, // retained in schema; no longer user-facing (defaults to 1)
    val createdAt: Long = System.currentTimeMillis()
)
