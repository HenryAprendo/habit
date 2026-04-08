package com.henrydev.habit.data.repository

import com.henrydev.habit.data.db.UserDao
import com.henrydev.habit.data.entities.UserProfileEntity
import com.henrydev.habit.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class OfflineUserRepository @Inject constructor(
    private val userDao: UserDao
): UserRepository {

    override fun getUserProfile(): Flow<UserProfileEntity> {
        return userDao.getUserProfile().map { profile ->
            if (profile == null) {
                val initialProfile = UserProfileEntity(id = 1, totalXp = 0, level = 1)
                userDao.saveProfile(initialProfile)
                initialProfile
            } else {
                profile
            }
        }
    }

    override suspend fun addXp(amount: Int) {
        userDao.getUserProfile().first()?.let { currentProfile ->
            val newXp = (currentProfile.totalXp + amount).coerceAtLeast(0)
            userDao.saveProfile(currentProfile.copy(totalXp = newXp))
        }
    }

}