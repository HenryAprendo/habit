package com.henrydev.habit.domain.use_cases

import com.henrydev.habit.domain.model.Habit
import com.henrydev.habit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetHomeHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {

    operator fun invoke(): Flow<List<HabitItemState>> {
        return habitRepository.getHabitsWithHistory().map { habitsWithHistory ->
            habitsWithHistory.map { item ->
                val isCompletedToday = item.history.any { log ->
                    isToday(log.date) && log.isCompleted
                }

                val lastSevenDays = (0..6).map { daysAgo ->
                    LocalDate.now()
                        .minusDays(daysAgo.toLong())
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }.reversed()

                val weeklyProgress = lastSevenDays.map { referenceDate ->
                    val isDayCompleted = item.history.any { log ->
                        isSameDay(log.date,referenceDate) && log.isCompleted
                    }
                    HabitDayState(
                        date = referenceDate,
                        isCompleted = isDayCompleted
                    )
                }

                val referenceDate = LocalDate.now()

                val sortedHistory = item.history.sortedByDescending { it.date }
                var streakCounter = 0
                var checkDate = referenceDate

                val referenceDateMillis = referenceDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val completedToday = sortedHistory.any {
                    isSameDay(it.date, referenceDateMillis) && it.isCompleted
                }

                if (!completedToday) {
                    checkDate = checkDate.minusDays(1)
                }

                for (dayOffset in 0..item.history.size) {
                    // 1. Obtenemos los milisegundos del inicio del día de la fecha actual de chequeo
                    val dateToFindMillis = checkDate
                        .atStartOfDay(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    // 2. Buscamos el log usando la función isSameDay que ya modernizamos
                    val logFound = sortedHistory.find { isSameDay(it.date, dateToFindMillis) }

                    if (logFound != null && logFound.isCompleted) {
                        streakCounter++
                        // 3. Restamos un día de forma inmutable y reasignamos
                        checkDate = checkDate.minusDays(1)
                    } else {
                        break
                    }
                }

                HabitItemState(
                    habit = item.habit,
                    isCompleted = isCompletedToday,
                    weeklyProgress = weeklyProgress,
                    streakCounter = streakCounter
                )

            }
        }
    }

}

private fun isToday(dateMillis: Long): Boolean {
    val today = LocalDate.now()
    val logDate = Instant.ofEpochMilli(dateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return today == logDate
}

private fun isSameDay(date1: Long, date2: Long): Boolean {
    val zone = ZoneId.systemDefault()
    val logDate1 = Instant.ofEpochMilli(date1).atZone(zone).toLocalDate()
    val logDate2 = Instant.ofEpochMilli(date2).atZone(zone).toLocalDate()
    return logDate1 == logDate2
}

data class HabitItemState(
    val habit: Habit,
    val isCompleted: Boolean,
    val weeklyProgress: List<HabitDayState>,
    val streakCounter: Int
)

data class HabitDayState(
    val date: Long,
    val isCompleted: Boolean
)
