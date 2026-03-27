package com.henrydev.habit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity
import com.henrydev.habit.data.model.HabitWithLogs
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Query("SELECT * from habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Transaction
    @Query("SELECT * FROM habits")
    fun getHabitsWithLogs(): Flow<List<HabitWithLogs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)

    @Transaction
    suspend fun replaceAllData(habitsWithLogs: List<Pair<HabitEntity, List<HabitLogEntity>>>) {
        clearAllHabits()
        habitsWithLogs.forEach { (habit, logs) ->
            val newIdLong = insertHabit(habit)
            if (newIdLong != -1L) {
                val newHabitId = newIdLong.toInt()
                val updatedLogs = logs.map { it.copy(habitId = newHabitId) }
                insertAllLogs(updatedLogs)
            }

        }
    }

    @Query("DELETE FROM habits")
    suspend fun clearAllHabits()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllLogs(logs: List<HabitLogEntity>)

}