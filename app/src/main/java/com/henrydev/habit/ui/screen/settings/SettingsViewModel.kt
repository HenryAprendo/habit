package com.henrydev.habit.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.subscription.model.UserStatus
import com.henrydev.habit.domain.subscription.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        subscriptionRepository.getUserStatus()
            .map { status -> SettingsUiState(status) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState()
            )
}

data class SettingsUiState(
    val userStatus: UserStatus = UserStatus.Free
)