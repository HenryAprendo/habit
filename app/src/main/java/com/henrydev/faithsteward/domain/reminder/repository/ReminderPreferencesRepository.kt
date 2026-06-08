package com.henrydev.faithsteward.domain.reminder.repository

import com.henrydev.faithsteward.domain.reminder.model.ReminderPreferences
import kotlinx.coroutines.flow.Flow

interface ReminderPreferencesRepository {
    val reminderPreferences: Flow<ReminderPreferences>
    suspend fun setEnabled(enabled: Boolean)
    suspend fun setTime(hour: Int, minute: Int)
}
