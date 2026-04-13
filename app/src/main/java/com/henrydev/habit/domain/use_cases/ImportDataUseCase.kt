package com.henrydev.habit.domain.use_cases

import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.repository.UserRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ImportDataUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository
) {
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
    }

    suspend operator fun invoke(jsonString: String): Result<Unit> {
        return try {
            val backup = jsonConfig.decodeFromString<BackupModel>(jsonString)

            userRepository.restoreProfile(
                totalXp = backup.userProfile.totalXp,
                level = backup.userProfile.level
            )

            val habitsToRestore = backup.habits.map { habitBackup ->
                val entity = HabitEntity(
                    name = habitBackup.name,
                    description = habitBackup.description,
                    frequency = habitBackup.frequency,
                    createdAt = System.currentTimeMillis()
                )
                val logs = habitBackup.history.map { logBackup ->
                    HabitLogEntity(
                        habitId = 0,
                        date = logBackup.date,
                        isCompleted = logBackup.isCompleted
                    )
                }
                Pair(entity,logs)
            }
            habitRepository.restoreBackup(habitsToRestore)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}