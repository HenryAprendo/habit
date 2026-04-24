package com.henrydev.faithsteward

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.henrydev.faithsteward.domain.billing.BillingService
import com.henrydev.faithsteward.ui.notifications.HabitNotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import android.util.Log

@HiltAndroidApp
class HabitTrackerApplication: Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var notificationHelper: HabitNotificationHelper

    @Inject lateinit var billingService: BillingService

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannels()
        // Sync subscription state with Google Play at every cold start so a user
        // whose subscription expired while the app was closed doesn't stay Pro locally.
        billingService.connect()
    }


}
