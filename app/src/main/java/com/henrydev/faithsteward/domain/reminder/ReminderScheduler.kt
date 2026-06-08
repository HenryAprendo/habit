package com.henrydev.faithsteward.domain.reminder

/**
 * Domain abstraction over the platform reminder scheduling mechanism.
 * Keeps the domain layer free of Android framework details (WorkManager).
 * Implemented by the framework layer (e.g. NotificationScheduler).
 */
interface ReminderScheduler {
    /** Schedules the recurring daily reminder at the given local time. */
    fun schedule(hour: Int, minute: Int)

    /** Cancels any previously scheduled daily reminder. */
    fun cancel()
}
