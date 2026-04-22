package com.henrydev.faithsteward.ui.screen.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrydev.faithsteward.data.billing.GooglePlayBillingManager
import com.henrydev.faithsteward.domain.billing.BillingService
import com.henrydev.faithsteward.domain.billing.PurchaseResult
import com.henrydev.faithsteward.domain.subscription.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val subscriptionRepository : SubscriptionRepository,
    private val billingService: BillingService
): ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    init {
        billingService.connect()
        observePurchaseResults()
    }

    private fun observePurchaseResults() {
        viewModelScope.launch {
            (billingService as? GooglePlayBillingManager)?.purchaseResult?.collect { result ->
                when(result) {
                    is PurchaseResult.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is PurchaseResult.Cancelled -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Purchase cancelled") }
                    }
                    is PurchaseResult.Error -> {
                        _uiState.update { it.copy(isLoading = false,  errorMessage = result.message) }
                    }
                    is PurchaseResult.Pending -> {
                        //Keep loading for pending payments
                    }
                }
            }
        }
    }

    fun purchasePro(activity: Activity, productId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            // Updated manager logic to receive the Activity context safely
            val result = (billingService as? GooglePlayBillingManager)?.purchaseProduct( activity, productId )

            if (result is PurchaseResult.Error) {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun restorePurchases() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // We ask the Billing Engine to check the local cache of Google Play
                val isPro = billingService.checkSubscriptionStatus()

                if (isPro) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "No active subscription found to restore"
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Error connecting to Store: ${e.message}"
                )}
            }
        }
    }

    companion object {
        const val PRODUCT_ID_MONTHLY = "pro_monthly_plan"
        const val PRODUCT_ID_ANNUAL = "pro_annual_plan"
    }

}

data class PaywallUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)