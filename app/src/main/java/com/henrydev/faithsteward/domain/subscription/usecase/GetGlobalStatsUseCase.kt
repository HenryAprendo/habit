package com.henrydev.faithsteward.domain.subscription.usecase

import com.henrydev.faithsteward.domain.repository.HabitRepository
import com.henrydev.faithsteward.domain.subscription.model.HabitStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
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
            // Group completed logs by calendar day (key = epoch day via LocalDate).
            val heatmap = allLogs
                .filter { it.isCompleted }
                .groupBy { toEpochDay(it.date) }
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

    /** Converts an epoch-millis timestamp to its local calendar day (epoch day). */
    private fun toEpochDay(timestamp: Long): Long =
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()

    /** Longest run of consecutive calendar days (epoch-day diff == 1). */
    private fun calculateBestStreak(sortedDays: List<Long>): Int {
        if (sortedDays.isEmpty()) return 0
        var maxStreak = 1
        var currentStreak = 1

        for (i in 0 until sortedDays.size - 1) {
            if (sortedDays[i + 1] - sortedDays[i] == 1L) {
                currentStreak++
            } else {
                maxStreak = maxOf(maxStreak, currentStreak)
                currentStreak = 1
            }
        }
        return maxOf(maxStreak, currentStreak)
    }

}