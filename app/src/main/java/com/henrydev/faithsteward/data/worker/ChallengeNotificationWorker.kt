package com.henrydev.faithsteward.data.worker

import android.content.Context
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

    override suspend fun doWork(): Result {
        try {
            val pendingChallenges = checkPendingChallengesUseCase()


// Technical Audit: Logging the amount of pending challenges found
            android.util.Log.d("ChallengeWorker", "Pending challenges found: ${pendingChallenges.size}")

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