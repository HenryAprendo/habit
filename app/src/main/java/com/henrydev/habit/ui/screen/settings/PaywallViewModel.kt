package com.henrydev.habit.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.habit.domain.subscription.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val subscriptionRepository : SubscriptionRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    fun purchasePro() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(1500)
            subscriptionRepository.updateSubscriptionStatus(true)
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }

}

data class PaywallUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)