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
import com.henrydev.faithsteward.domain.reminder.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Framework implementation of [ReminderScheduler] using WorkManager.
 * Pure scheduling mechanics — orchestration (reading user preferences,
 * deciding whether to schedule or cancel) lives in the domain use cases.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    override fun schedule(hour: Int, minute: Int) {
        android.util.Log.d("NotificationScheduler", "schedule() at $hour:$minute")

        // Cancel first so a time change deterministically takes effect.
        cancel()

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresDeviceIdle(false)
            .build()

        val delay = calculateInitialDelay(hour, minute)

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

            // We just cancelled any existing schedule, so KEEP is effectively a fresh enqueue.
            workManager.enqueueUniqueWork(
                "challenge_reminder_once",
                ExistingWorkPolicy.KEEP,
                oneTimeRequest
            )
            android.util.Log.d("NotificationScheduler", "One-time work enqueued, delay=${delay / 1000 / 60} min")

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

    override fun cancel() {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("challenge_reminder_once")
        workManager.cancelUniqueWork("challenge_reminder_periodic")
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        var target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

        if (now.isAfter(target)) {
            target = target.plusDays(1)
        }

        val delay = Duration.between(now, target).toMillis()
        val safeDelay = if (delay <= 0) 60000L else delay

        android.util.Log.d("NotificationScheduler", "Calculated delay: ${safeDelay / 1000 / 60} min")
        return safeDelay
    }

}
