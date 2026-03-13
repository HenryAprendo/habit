package com.henrydev.habit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity

@Database(
    entities = [HabitEntity::class, HabitLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var Instance: HabitDatabase? = null

        /**
         * Retorna la instancia de la base de datos o la crea si no existe.
         * El uso de synchronized garantiza que no se creen dos instancias en hilos diferentes.
         */
        fun getDatabase(context: Context): HabitDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    /**
                     * Estrategia de migración: Si cambias el esquema (añadir columnas),
                     * esto borrará los datos. Para producción se usan migraciones reales.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}