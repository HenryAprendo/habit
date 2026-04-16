package com.henrydev.faithsteward.ui.notifications

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
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
        // Contrainst: Optimize for battery life
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(false)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ChallengeNotificationWorker>(
            24,
            TimeUnit.HOURS,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraint)
            .setInitialDelay(calculateInitialDelay(),TimeUnit.MILLISECONDS)
            .addTag("challenge_reminder_tag")
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "challenge_reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        var target = now.withHour(18).withMinute(0).withSecond(0).withNano(0)

        if (now.isAfter(target)) {
            target = target.plusDays(1)
        }

        val delay = Duration.between(now,target).toMillis()

        // Log de Auditoría Técnica para ver el delay real en Logcat
        android.util.Log.d("NotificationScheduler", "Scheduled for 18:00. Delay: ${delay / 1000 / 60} min")
        return delay
    }

}
