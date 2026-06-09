package com.henrydev.faithsteward.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.faithsteward.domain.onboarding.usecase.CompleteOnboardingUseCase
import com.henrydev.faithsteward.domain.onboarding.usecase.GetOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    // null = still loading the persisted flag; avoids flashing onboarding for returning users.
    val completed: StateFlow<Boolean?> = getOnboardingCompletedUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun complete() {
        viewModelScope.launch { completeOnboardingUseCase() }
    }
}
