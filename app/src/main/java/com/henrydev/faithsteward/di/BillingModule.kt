package com.henrydev.faithsteward.di

import com.henrydev.faithsteward.data.billing.GooglePlayBillingManager
import com.henrydev.faithsteward.domain.billing.BillingService
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