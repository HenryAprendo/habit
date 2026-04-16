package com.henrydev.faithsteward.domain.subscription.usecase


import com.henrydev.faithsteward.domain.model.DayStatus
import com.henrydev.faithsteward.domain.model.HabitProgressDetail
import com.henrydev.faithsteward.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetHabitsProgressUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<List<HabitProgressDetail>> {
        return habitRepository.getHabitsWithHistory().map { habitsWithHistory ->
            habitsWithHistory.map { item ->

                val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
                val completedInLastMonth = item.history.count {
                    it.date >= thirtyDaysAgo && it.isCompleted
                }
                val rate = completedInLastMonth.toFloat() / 30f

                // Map logs to a Set of completed dates
                val completedDates = item.history
                    .filter { it.isCompleted }
                    .map {
                        Instant.ofEpochMilli(it.date)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }.toSet()

                // Calculate the currentStreak
                var currentStreak = 0
                var dateToCheck = LocalDate.now()

                if (!completedDates.contains(dateToCheck)) {
                    dateToCheck = dateToCheck.minusDays(1)
                }

                while (completedDates.contains(dateToCheck)) {
                    currentStreak++
                    dateToCheck = dateToCheck.minusDays(1)
                }

                //Calculate de best streak
                val sortedDates = completedDates.toList().sorted()
                var bestStreak = 0
                var tempStreak = 0
                var lastDate: LocalDate? = null

                for (currentDate in sortedDates) {
                    if (lastDate != null && currentDate == lastDate.plusDays(1)) {
                        //Succesive day found
                        tempStreak++
                    } else {
                        tempStreak = 1
                    }
                    bestStreak = maxOf(bestStreak,tempStreak)
                    lastDate = currentDate
                }

                //Calculate last seven days activity (The weekly trend)
                val lastSevenDays = (0 until 7).map { dayOffset ->
                    val date = LocalDate.now().minusDays(dayOffset.toLong())
                    DayStatus(
                        date = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        isCompleted = completedDates.contains(date)
                    )
                }.reversed()

                HabitProgressDetail(
                    habitId = item.habit.id,
                    habitName = item.habit.name,
                    completionRate = rate.coerceIn(0f, 1f),
                    currentStreak = currentStreak,
                    bestStreak = bestStreak,
                    totalCompletions = item.history.count { it.isCompleted },
                    lastSevenDays = lastSevenDays
                )
            }
        }
    }
}