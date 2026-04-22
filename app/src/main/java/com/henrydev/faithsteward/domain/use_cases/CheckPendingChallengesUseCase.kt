package com.henrydev.faithsteward.domain.use_cases

import com.henrydev.faithsteward.domain.repository.ChallengeRepository
import com.henrydev.faithsteward.domain.repository.HabitRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CheckPendingChallengesUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(): List<String> {
        val today = LocalDate.now()
        val activeSubscription = challengeRepository.getActiveSubscriptions().first()
        val habitsWithHistory = habitRepository.getHabitsWithHistory().first()

        return activeSubscription.filter { subscription ->
            val habitEntry = habitsWithHistory.find { it.habit.id == subscription.linkedHabitId }
            val isCompletedToday = habitEntry?.history?.any { log ->
                val logDate = Instant.ofEpochMilli(log.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                today.isEqual(logDate) && log.isCompleted
            } ?: false
            !isCompletedToday
        }.map { subscription ->
            "challenge #${subscription.challengeId}"
        }

    }
}









