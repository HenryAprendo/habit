package com.henrydev.faithsteward.domain.use_cases

import com.henrydev.faithsteward.domain.repository.HabitRepository
import com.henrydev.faithsteward.domain.subscription.model.UserStatus
import com.henrydev.faithsteward.domain.subscription.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class CanCreateHabitUseCase @Inject constructor (
    private val habitRepository: HabitRepository,
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return combine(
            habitRepository.getAllHabits(),
            subscriptionRepository.getUserStatus()
        ) { habits, status ->
            when(status) {
                is UserStatus.Pro -> true
                is UserStatus.Free -> habits.size < 3
            }
        }
    }
}