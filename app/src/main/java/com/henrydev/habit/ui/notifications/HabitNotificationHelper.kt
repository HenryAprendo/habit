package com.henrydev.habit.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import android.os.Build
import androidx.core.app.NotificationCompat
import com.henrydev.habit.R
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class HabitNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_CHALLENGES_ID = "challenges_reminders"
        const val NOTIFICATION_ID = 1001
    }

    /**
     * Creates the mandatory Notification Channels for Android 8.0+
     * This should be called during de App Startup (MainActivity)
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Challenge Reminders"
            val descriptionText = "Notifications to remind you of pending habit in your active challenges"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(
                CHANNEL_CHALLENGES_ID,
                name,
                importance
            ).apply { description = descriptionText }

            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds and displays a notification to the user
     */
    fun showChallengeReminder(title: String, message: String) {

        val notification = NotificationCompat.Builder(context,CHANNEL_CHALLENGES_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID,notification)
    }

}
