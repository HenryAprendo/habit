package com.henrydev.habit.domain.use_cases

import com.henrydev.habit.domain.model.Challenge
import com.henrydev.habit.domain.model.ChallengeProgress
import com.henrydev.habit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChallengeProgressUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(
        challenge: Challenge,
        linkedHabitId: Long,
        startDate: Long
    ): Flow<ChallengeProgress> {
        return habitRepository.getHabitsWithHistory().map { habits ->
            val habitWithHistory = habits.find { it.habit.id == linkedHabitId }
            val challengeLogs = habitWithHistory?.history?.filter { it.date >= startDate } ?: emptyList()
            val completedDays = challengeLogs.count { it.isCompleted }
            val progress = (completedDays.toFloat() / challenge.durationDays).coerceIn(0f,1f)
            val remaining = challenge.durationDays - completedDays

            ChallengeProgress(
                challengeId = challenge.id,
                linkedHabitId = linkedHabitId,
                currentStreak = 0,
                completedDays = completedDays,
                totalDays = challenge.durationDays,
                progressPercentage = progress,
                daysRemaining = if (remaining > 0) remaining else 0,
                isCompleted = completedDays >= challenge.durationDays
            )
        }
    }
}