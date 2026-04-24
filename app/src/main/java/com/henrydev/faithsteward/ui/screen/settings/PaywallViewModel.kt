package com.henrydev.faithsteward.ui.screen.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            billingService.purchaseResult.collect { result ->
                when(result) {
                    is PurchaseResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isPendingPayment = false,
                                errorMessage = null
                            )
                        }
                        billingService.consumePurchaseResult()
                    }
                    is PurchaseResult.Cancelled -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isPendingPayment = false,
                                errorMessage = "Purchase cancelled"
                            )
                        }
                        billingService.consumePurchaseResult()
                    }
                    is PurchaseResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isPendingPayment = false,
                                errorMessage = result.message
                            )
                        }
                        billingService.consumePurchaseResult()
                    }
                    is PurchaseResult.Pending -> {
                        // Payment awaiting confirmation (parental approval, slow
                        // payment methods, etc). Keep the paywall open and inform
                        // the user — the final Success/Error will arrive later.
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isPendingPayment = true,
                                errorMessage = null
                            )
                        }
                    }
                }
            }
        }
    }

    fun purchasePro(activity: Activity, productId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, isPendingPayment = false) }
        viewModelScope.launch {
            val result = billingService.purchaseProduct(activity, productId)

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
    val isPendingPayment: Boolean = false,
    val errorMessage: String? = null
)
