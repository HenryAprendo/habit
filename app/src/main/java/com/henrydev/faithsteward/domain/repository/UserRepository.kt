package com.henrydev.faithsteward.domain.repository

import com.henrydev.faithsteward.data.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfileEntity?>
    suspend fun addXp(amount: Int)
    suspend fun restoreProfile(totalXp: Long, level: Int)
}