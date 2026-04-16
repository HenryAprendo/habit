package com.henrydev.faithsteward.di

import com.henrydev.faithsteward.data.repository.OfflineChallengeRepository
import com.henrydev.faithsteward.data.repository.OfflineHabitRepository
import com.henrydev.faithsteward.data.repository.OfflineUserRepository
import com.henrydev.faithsteward.domain.repository.ChallengeRepository
import com.henrydev.faithsteward.domain.repository.HabitRepository
import com.henrydev.faithsteward.domain.repository.UserRepository
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