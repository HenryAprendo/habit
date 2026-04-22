package com.henrydev.faithsteward.ui.notifications

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.henrydev.faithsteward.data.worker.ChallengeNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleDailyReminder() {
        android.util.Log.d("NotificationScheduler", "scheduleDailyReminder() called")

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresDeviceIdle(false)
            .build()

        val delay = calculateInitialDelay()

        // One-time request for the first notification (fires at the target hour today/tomorrow)
        val oneTimeRequest = OneTimeWorkRequestBuilder<ChallengeNotificationWorker>()
            .setConstraints(constraint)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("challenge_reminder_tag")
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        // Periodic request for daily recurrence (subsequent days)
        val periodicRequest = PeriodicWorkRequestBuilder<ChallengeNotificationWorker>(
            24, TimeUnit.HOURS,
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraint)
            .setInitialDelay(delay + TimeUnit.HOURS.toMillis(24), TimeUnit.MILLISECONDS)
            .addTag("challenge_reminder_tag")
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        try {
            val workManager = WorkManager.getInstance(context)

            // Schedule one-time: KEEP avoids rescheduling if already pending
            workManager.enqueueUniqueWork(
                "challenge_reminder_once",
                ExistingWorkPolicy.KEEP,
                oneTimeRequest
            )
            android.util.Log.d("NotificationScheduler", "One-time work enqueued, delay=${delay / 1000 / 60} min")

            // Schedule periodic: KEEP so it doesn't reset on each app open
            workManager.enqueueUniquePeriodicWork(
                "challenge_reminder_periodic",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
            android.util.Log.d("NotificationScheduler", "Periodic work enqueued, first run in ~${(delay + TimeUnit.HOURS.toMillis(24)) / 1000 / 60 / 60} hours")

        } catch (e: Exception) {
            android.util.Log.e("NotificationScheduler", "Failed to enqueue work: ${e.message}", e)
        }
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        var target = now.withHour(9).withMinute(0).withSecond(0).withNano(0)

        if (now.isAfter(target)) {
            target = target.plusDays(1)
        }

        val delay = Duration.between(now, target).toMillis()
        val safeDelay = if (delay <= 0) 60000L else delay

        android.util.Log.d("NotificationScheduler", "Calculated delay: ${safeDelay / 1000 / 60} min")
        return safeDelay
    }

}