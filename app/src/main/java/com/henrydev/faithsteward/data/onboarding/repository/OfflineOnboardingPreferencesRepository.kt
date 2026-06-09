package com.henrydev.faithsteward.data.onboarding.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.henrydev.faithsteward.domain.onboarding.repository.OnboardingPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineOnboardingPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : OnboardingPreferencesRepository {

    private object Keys {
        val COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    override val onboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[Keys.COMPLETED] ?: false }

    override suspend fun setCompleted() {
        dataStore.edit { it[Keys.COMPLETED] = true }
    }
}
