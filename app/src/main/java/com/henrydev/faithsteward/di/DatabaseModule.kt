package com.henrydev.faithsteward.di

import android.content.Context
import androidx.room.Room
import com.henrydev.faithsteward.data.db.ChallengeDao
import com.henrydev.faithsteward.data.db.HabitDao
import com.henrydev.faithsteward.data.db.HabitDatabase
import com.henrydev.faithsteward.data.db.HabitDatabaseCallback
import com.henrydev.faithsteward.data.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        challengeDaoProvider: Provider<ChallengeDao>,
        habitDaoProvider: Provider<HabitDao>
    ): HabitDatabase {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            "habit_database"
        )
            .addCallback(
                HabitDatabaseCallback(
                    scope = scope,
                    challengeDaoProvider = challengeDaoProvider,
                    habitDaoProvider = habitDaoProvider
                )
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideChallengeDao(database: HabitDatabase): ChallengeDao {
        return database.challengeDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: HabitDatabase): UserDao {
        return database.userDao()
    }

}