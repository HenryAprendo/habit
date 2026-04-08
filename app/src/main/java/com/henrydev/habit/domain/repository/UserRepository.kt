package com.henrydev.habit.domain.repository

import com.henrydev.habit.data.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfileEntity>
    suspend fun addXp(amount: Int)
}