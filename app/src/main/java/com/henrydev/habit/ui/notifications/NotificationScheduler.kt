package com.henrydev.habit.ui.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.henrydev.habit.data.worker.ChallengeNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleDailyReminder() {
        // Contrainst: Optimize for battery life
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ChallengeNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraint)
            .setInitialDelay(calculateInitialDelay(),TimeUnit.MILLISECONDS)
            .addTag("challenge_reminder_tag")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "challenge_reminder_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        var target = now.withHour(20).withMinute(0).withSecond(0).withNano(0)

        if (now.isAfter(target)) {
            target = target.plusDays(1)
        }
        return java.time.Duration.between(now,target).toMillis()
    }

}
