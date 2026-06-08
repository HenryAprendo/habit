package com.henrydev.faithsteward.domain.onboarding.usecase

import com.henrydev.faithsteward.domain.onboarding.repository.OnboardingPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingCompletedUseCase @Inject constructor(
    private val repository: OnboardingPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.onboardingCompleted
}
