package com.henrydev.faithsteward.domain.onboarding.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingPreferencesRepository {
    /** Emits whether the user has already completed the first-run onboarding. */
    val onboardingCompleted: Flow<Boolean>
    suspend fun setCompleted()
}
