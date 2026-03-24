package com.henrydev.habit.domain.subscription.repository

import com.henrydev.habit.domain.subscription.model.UserStatus
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    /**
     * Streams the current user status reactively.
     */
    fun getUserStatus(): Flow<UserStatus>

    /**
     * Updates the user status
     */
    suspend fun updateSubscriptionStatus(isPro: Boolean)
}