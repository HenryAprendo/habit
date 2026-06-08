package com.henrydev.faithsteward.data.reminder.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.henrydev.faithsteward.domain.reminder.model.ReminderPreferences
import com.henrydev.faithsteward.domain.reminder.repository.ReminderPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Stores the daily reminder configuration in the same Preferences DataStore
 * used for the subscription flag, so we don't add a second datastore file
 * (which would orphan existing users' is_pro_user value).
 */
class OfflineReminderPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ReminderPreferencesRepository {

    private object Keys {
        val ENABLED = booleanPreferencesKey("reminder_enabled")
        val HOUR = intPreferencesKey("reminder_hour")
        val MINUTE = intPreferencesKey("reminder_minute")
    }

    override val reminderPreferences: Flow<ReminderPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            ReminderPreferences(
                enabled = preferences[Keys.ENABLED] ?: true,
                hour = preferences[Keys.HOUR] ?: 9,
                minute = preferences[Keys.MINUTE] ?: 0
            )
        }

    override suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.ENABLED] = enabled }
    }

    override suspend fun setTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.HOUR] = hour
            preferences[Keys.MINUTE] = minute
        }
    }
}
