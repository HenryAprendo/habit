package com.henrydev.faithsteward.domain.subscription.usecase

import com.henrydev.faithsteward.domain.repository.HabitRepository
import com.henrydev.faithsteward.domain.subscription.model.HabitStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetGlobalStatsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<HabitStats> {
        return habitRepository.getHabitsWithHistory().map { habitsWithHistory ->
            if (habitsWithHistory.isEmpty()) {
                return@map HabitStats(0f,0,0,emptyMap())
            }

            val allLogs = habitsWithHistory.flatMap { it.history }
            val totalPossibleLogs = habitsWithHistory.size * 30
            val completedLogs = allLogs.count { it.isCompleted }

            //Cálculo de la taza de finalización
            val rate = if (totalPossibleLogs > 0)
                completedLogs.toFloat() / totalPossibleLogs else 0f
            //Procesamiento para Heatmap (agrupado por dia)
            //Agrupamos todos los logs por fecha (sin hora)
            val heatmap = allLogs
                .filter { it.isCompleted }
                .groupBy { truncateDate(it.date) }
                .mapValues { entry ->
                    entry.value.isNotEmpty()
                }

            val bestStreak = calculateBestStreak(heatmap.keys.sorted())

            HabitStats(
                totalCompletionRate = rate,
                perfectDaysCount = heatmap.size,
                bestStreakRecord = bestStreak,
                heatmapData = heatmap
            )
        }
    }

    private fun truncateDate(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }
        return cal.timeInMillis
    }

    private fun calculateBestStreak(sortedDates: List<Long>): Int {
        if (sortedDates.isEmpty()) return 0
        var maxStreak = 1
        var currentStreak = 1

        for (i in 0 until sortedDates.size - 1) {
            val diff = sortedDates[i+1] - sortedDates[i]
            val oneDay = 24 * 60 * 60 * 1000L
            if (diff <= oneDay + 1000) {
                currentStreak++
            } else {
                maxStreak = maxOf(maxStreak,currentStreak)
                currentStreak = 1
            }
        }
        return maxOf(maxStreak,currentStreak)
    }

}