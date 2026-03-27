package com.henrydev.habit.domain.use_cases

import com.henrydev.habit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    private val jsonConfig = Json { prettyPrint = true }

    suspend operator fun invoke(): String {
        val allData = habitRepository.getHabitsWithHistory().first()
        //Mapeamos una estructura serializable
        val backup = BackupModel(
            version = 1,
            exportDate = System.currentTimeMillis(),
            habits = allData.map { habitWithHistory ->
                HabitBackup(
                    name = habitWithHistory.habit.name,
                    description = habitWithHistory.habit.description,
                    frequency = habitWithHistory.habit.frequency,
                    history = habitWithHistory.history.map { log ->
                        LogBackup(date = log.date, isCompleted = log.isCompleted)
                    }
                )
            }
        )

        return jsonConfig.encodeToString(backup)
    }
}

@Serializable
data class BackupModel(
    val version: Int,
    val exportDate: Long,
    val habits:  List<HabitBackup>
)

@Serializable
data class  HabitBackup(
    val name: String,
    val description: String,
    val frequency: Int,
    val history: List<LogBackup>
)

@Serializable
data class LogBackup(
    val date: Long,
    val isCompleted: Boolean
)



