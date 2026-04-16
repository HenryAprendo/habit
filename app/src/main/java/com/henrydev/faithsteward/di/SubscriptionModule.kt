package com.henrydev.faithsteward.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.henrydev.faithsteward.data.subscription.repository.OfflineSubscriptionRepository
import com.henrydev.faithsteward.domain.subscription.repository.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SubscriptionModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        imp: OfflineSubscriptionRepository
    ): SubscriptionRepository

    companion object {
        private const val SUBSCRIPTION_PREFERENCES = "subscription_prefs"

        @Provides
        @Singleton
        fun provideSubscriptionDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile(SUBSCRIPTION_PREFERENCES) }
            )
        }

    }

}