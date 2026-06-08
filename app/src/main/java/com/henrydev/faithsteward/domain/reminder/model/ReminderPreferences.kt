package com.henrydev.faithsteward.domain.reminder.model

/**
 * User-configurable daily reminder settings.
 * Persisted in DataStore. Defaults to an enabled reminder at 09:00.
 */
data class ReminderPreferences(
    val enabled: Boolean = true,
    val hour: Int = 9,
    val minute: Int = 0
)
