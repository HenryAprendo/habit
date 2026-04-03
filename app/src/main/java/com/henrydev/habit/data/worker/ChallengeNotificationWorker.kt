package com.henrydev.habit.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.henrydev.habit.domain.use_cases.CheckPendingChallengesUseCase
import com.henrydev.habit.ui.notifications.HabitNotificationHelper
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
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }

    }

}