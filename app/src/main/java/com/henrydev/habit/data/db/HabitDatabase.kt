package com.henrydev.habit.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.henrydev.habit.data.db.HabitDao
import com.henrydev.habit.data.entities.ChallengeEntity
import com.henrydev.habit.data.entities.ChallengeSubscriptionEntity
import com.henrydev.habit.data.entities.HabitEntity
import com.henrydev.habit.data.entities.HabitLogEntity
import com.henrydev.habit.data.entities.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Provider

@Database(
    entities = [
        HabitEntity::class,
        HabitLogEntity::class,
        ChallengeEntity::class,
        ChallengeSubscriptionEntity::class,
        UserProfileEntity::class ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ChallengeConverters::class)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun userDao(): UserDao

}

class HabitDatabaseCallback(
    private val scope: CoroutineScope,
    private val challengeDaoProvider: Provider<ChallengeDao>,
    private val habitDaoProvider: Provider<HabitDao>
): RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        scope.launch {
            val challengeDao = challengeDaoProvider.get()
            if (challengeDao.getAnyChallenge() == null){
                ChallengeSeeds.DEFAULT_CHALLENGES.forEach {
                    challengeDao.insertChallenge(it)
                }
            }

            val habitDao = habitDaoProvider.get()
            if (habitDao.getAnyHabit() == null) {
                HabitSeeds.DEFAULT_SPIRITUAL_HABITs.forEach {
                    habitDao.insertHabit(it)
                }
            }

        }
    }
}




















