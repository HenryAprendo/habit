package com.henrydev.habit.di

import com.henrydev.habit.data.billing.GooglePlayBillingManager
import com.henrydev.habit.domain.billing.BillingService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for Billing Infrastructure.
 * This ensures a single instance of the Billing Engine exists throughout the app.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    @Binds
    @Singleton
    abstract fun bindBillingService(
        googlePlayBillingManager: GooglePlayBillingManager
    ): BillingService

}