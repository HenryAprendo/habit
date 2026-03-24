package com.henrydev.habit.data.subscription.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.henrydev.habit.domain.subscription.model.UserStatus
import com.henrydev.habit.domain.subscription.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineSubscriptionRepository @Inject constructor (
    private val dataStore: DataStore<Preferences>
) : SubscriptionRepository {

    private object PreferencesKeys {
        val IS_PRO_USER = booleanPreferencesKey("is_pro_user")
    }

    override fun getUserStatus(): Flow<UserStatus> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val isPro = preferences[PreferencesKeys.IS_PRO_USER] ?: false
            if(isPro) UserStatus.Pro else UserStatus.Free
        }

    override suspend fun updateSubscriptionStatus(isPro: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PRO_USER] = isPro
        }
    }

}