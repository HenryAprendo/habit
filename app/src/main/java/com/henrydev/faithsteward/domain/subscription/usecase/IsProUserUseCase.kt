package com.henrydev.faithsteward.domain.subscription.usecase

import com.henrydev.faithsteward.domain.subscription.model.UserStatus
import com.henrydev.faithsteward.domain.subscription.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsProUserUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.getUserStatus().map {
            it is UserStatus.Pro
        }
    }
}