package com.henrydev.habit.di

import com.henrydev.habit.data.repository.OfflineChallengeRepository
import com.henrydev.habit.data.repository.OfflineHabitRepository
import com.henrydev.habit.data.repository.OfflineUserRepository
import com.henrydev.habit.domain.repository.ChallengeRepository
import com.henrydev.habit.domain.repository.HabitRepository
import com.henrydev.habit.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        offlineHabitRepository: OfflineHabitRepository
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindChallengeRepository(
        offlineChallengeRepository: OfflineChallengeRepository
    ): ChallengeRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        offlineUserRepository: OfflineUserRepository
    ): UserRepository

}