package com.henrydev.habit.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity
import com.henrydev.habit.data.model.HabitWithLogs
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Query("SELECT * from habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Transaction
    @Query("SELECT * FROM habits")
    fun getHabitsWithLogs(): Flow<List<HabitWithLogs>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)

    @Transaction
    suspend fun replaceAllData(habitsWithLogs: List<Pair<HabitEntity, List<HabitLogEntity>>>) {

        habitsWithLogs.forEach { (habit, logs) ->
            // 1. Try to find if the habit already exists by name (Logical Unique Key)
            val existingHabit = getHabitByName(habit.name)

            val targetHabitId: Long = if (existingHabit != null) {
                // 2. If it exists, we update its metadata but keep its ID
                updateHabit(habit.copy(id = existingHabit.id))
                existingHabit.id
            } else {
                // 3. If it's new, we insert it
                insertHabit(habit)
            }

            // 4. Insert logs associated with this habit ID
            if (targetHabitId != -1L) {
                val updatedLogs = logs.map { it.copy(habitId = targetHabitId) }
                insertAllLogs(updatedLogs)
            }
        }
    }

    // Needed for the logic above
    @Query("SELECT * FROM habits WHERE name = :name LIMIT 1")
    suspend fun getHabitByName(name: String): HabitEntity?

    @Query("DELETE FROM habits")
    suspend fun clearAllHabits()

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertAllLogs(logs: List<HabitLogEntity>)

    @Query("SELECT * FROM habits LIMIT 1")
    suspend fun getAnyHabit(): HabitEntity?

}