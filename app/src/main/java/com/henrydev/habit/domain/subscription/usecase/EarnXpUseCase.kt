package com.henrydev.habit.domain.subscription.usecase

import com.henrydev.habit.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EarnXpUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val  isProUserUseCase: IsProUserUseCase
) {

    companion object {
        const val BASE_HABIT_XP = 10
        const val BASE_HABIT_XP_PRO = 12
        const val CHALLENGE_COMPLETED_XP = 100
        const val PRO_STREAK_BONUS_MULTIPLIER = 2
    }

    /**
     * Call this when a habit is completed.
     * @param currentStreak The current streak of the habit to apply PRO bonuses.
     */

    suspend fun onHabitCompleted(currentStreak: Int = 0) {
        val isPro = isProUserUseCase().first()

        val baseReward = if (isPro) BASE_HABIT_XP_PRO else BASE_HABIT_XP
        var totalXp = baseReward
        if (isPro && currentStreak > 0) {
            // PRO users get a bonus based on their commitment (streak)
            val streakBonus = currentStreak * PRO_STREAK_BONUS_MULTIPLIER
            totalXp += streakBonus
        }
        userRepository.addXp(totalXp)
    }

    /**
     * Call this when a challenge is successfully finished.
     */
    suspend fun onChallengeCompleted() {
        val isPro = isProUserUseCase().first()

        // Challenges give a massive boost, PROs could get a 1.5x multiplier
        val multiplier = if (isPro) 1.5 else 1.0
        val finalXp = (CHALLENGE_COMPLETED_XP * multiplier).toInt()
        userRepository.addXp(finalXp)
    }

    /**
     * Penalty logic (Optional: removing XP if a habit is unchecked)
     */
    suspend fun onHabitUncheked() {
        userRepository.addXp(-BASE_HABIT_XP)
    }

}