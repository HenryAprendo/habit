package com.henrydev.faithsteward.domain.onboarding.usecase

import com.henrydev.faithsteward.domain.onboarding.repository.OnboardingPreferencesRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val repository: OnboardingPreferencesRepository
) {
    suspend operator fun invoke() = repository.setCompleted()
}
