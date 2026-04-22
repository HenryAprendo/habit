package com.henrydev.faithsteward.data.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.henrydev.faithsteward.domain.use_cases.CheckPendingChallengesUseCase
import com.henrydev.faithsteward.ui.notifications.HabitNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ChallengeNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: HabitNotificationHelper,
    private val checkPendingChallengesUseCase: CheckPendingChallengesUseCase
): CoroutineWorker(context,workerParams) {

    init {
        android.util.Log.d("ChallengeWorker", "INTERNAL LOG: Class instantiated by System")
    }


    override suspend fun doWork(): Result {
        android.util.Log.d("ChallengeWorker", "doWork() triggered by System")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                android.util.Log.w("ChallengeWorker", "POST_NOTIFICATIONS permission not granted, skipping")
                return Result.success()
            }

            android.util.Log.d("ChallengeWorker", "Firing notification")
            val pendingChallenges = checkPendingChallengesUseCase()

            if (pendingChallenges.isNotEmpty()) {
                val notificationTitle = "Don't break your streak!"
                val notificationMessage = if (pendingChallenges.size == 1) {
                    "You have 1 active challenge waiting for you today"
                } else {
                    "You have ${pendingChallenges.size} challenges to complete before the day ends"
                }
                notificationHelper.showChallengeReminder(
                    title = notificationTitle,
                    message = notificationMessage
                )
            } else {
                android.util.Log.d("ChallengeWorker", "Notification skipped: All disciplines are up to date.")
            }
            return Result.success()
        } catch (e: Exception) {
            android.util.Log.e("ChallengeWorker", "Error executing worker: ${e.message}")
            return Result.retry()
        }

    }

}