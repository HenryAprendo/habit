package com.henrydev.faithsteward.domain.use_cases

import com.henrydev.faithsteward.domain.model.Challenge
import com.henrydev.faithsteward.domain.repository.ChallengeRepository
import com.henrydev.faithsteward.domain.subscription.model.UserStatus
import com.henrydev.faithsteward.domain.subscription.repository.SubscriptionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class JoinChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend operator fun invoke(challenge: Challenge, linkedHabitId: Long): Result<Unit> {
        val userStatus = subscriptionRepository.getUserStatus().first()
        if (challenge.isPro && userStatus is UserStatus.Free) {
            return Result.failure(Exception("This challenge requires a Pro Subscription"))
        }
        return try {
            challengeRepository.subscribeToChallenge(challenge.id, linkedHabitId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}