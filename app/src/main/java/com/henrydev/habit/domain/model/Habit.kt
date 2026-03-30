package com.henrydev.habit.domain.model


data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String,
    val frequency: Int, // Ej: veces por semana
    val createdAt: Long = System.currentTimeMillis()
)
