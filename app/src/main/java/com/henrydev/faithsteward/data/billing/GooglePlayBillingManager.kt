package com.henrydev.faithsteward.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.henrydev.faithsteward.domain.billing.BillingService
import com.henrydev.faithsteward.domain.billing.PurchaseResult
import com.henrydev.faithsteward.domain.subscription.repository.SubscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.android.billingclient.api.AcknowledgePurchaseParams
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.coroutines.resume

/**
 * Technical Implementation of Google Play Billing Library.
 * Manages the connection lifecycle and transforms Google's internal responses
 * into our clean Domain models.
 */
@Singleton
class GooglePlayBillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionRepository: SubscriptionRepository
): BillingService, PurchasesUpdatedListener {

    // replay=1 so the last purchase result survives Activity recreation during
    // Google Play's biometric/password auth flow (when onPause destroys the Activity).
    private val _purchaseResult = MutableSharedFlow<PurchaseResult>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val purchaseResult: SharedFlow<PurchaseResult> = _purchaseResult.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    override fun connect() {
        if (billingClient.isReady) {
            _isReady.value = true
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                _isReady.value = false
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isReady.value = true
                    // Sync subscription status as soon as we connect
                    scope.launch { checkSubscriptionStatus() }
                }
            }
        })
    }

    /**
     * Launches the native Google Play purchase flow.
     * Note: This is a simplified version to establish the architecture.
     */
    override suspend fun purchaseProduct(activity: Activity, productId: String): PurchaseResult {
        val productDetails = queryProductDetails(productId) ?: return PurchaseResult.Error("Product not found")

        // We select the first offer (which usually contains the 7-day Free Trial)
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?:
        return PurchaseResult.Error("No valid offer found")

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        return withContext(Dispatchers.Main) {
            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Flow launched; the real result comes via onPurchasesUpdated.
                PurchaseResult.Pending
            } else {
                PurchaseResult.Error(billingResult.debugMessage)
            }
        }
    }

    /**
     * Critical for Stewardship: Verifies if the user is already PRO
     * by checking the Google Play cache. Suspends until the async query
     * callback returns, so the return value reflects the real state.
     */
    override suspend fun checkSubscriptionStatus(): Boolean {
        if (!billingClient.isReady) return false

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
                val isPro = if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                } else {
                    false
                }
                scope.launch {
                    subscriptionRepository.updateSubscriptionStatus(isPro)
                }
                if (continuation.isActive) continuation.resume(isPro)
            }
        }
    }

    override suspend fun getProductPrice(productId: String): String? {
        val productDetails = queryProductDetails(productId)
        return productDetails?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice
    }

    /**
     * Clears the replay cache after the UI has handled a terminal purchase result
     * (Success / Cancelled / Error). This prevents stale events from firing when
     * the user reopens the paywall later.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun consumePurchaseResult() {
        _purchaseResult.resetReplayCache()
    }

    /**
     * Private helper to fetch full product information from Google.
     * Essential for initiating purchases and verifying Free Trial eligibility.
     */
    private suspend fun queryProductDetails(productId: String): ProductDetails? {
        if (!billingClient.isReady) connect()
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        return withContext(Dispatchers.IO) {
            val result = billingClient.queryProductDetails(params)
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                result.productDetailsList?.firstOrNull()
            } else {
                null
            }
        }
    }

    override fun endConnection() {
        if (billingClient.isReady) {
            billingClient.endConnection()
            _isReady.value = false
        }
    }

    /**
     * Listener called by Google whenever a purchase is updated (success/fail).
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase?>?
    ) {
        when(billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    if (purchase != null) {
                        when (purchase.purchaseState) {
                            Purchase.PurchaseState.PURCHASED -> {
                                scope.launch { acknowledgePurchase(purchase) }
                            }
                            Purchase.PurchaseState.PENDING -> {
                                // Google Play requires us to surface pending state
                                // (e.g. awaiting parental approval, slow payment methods).
                                scope.launch {
                                    _purchaseResult.emit(PurchaseResult.Pending)
                                }
                            }
                        }
                    }
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                scope.launch {
                    _purchaseResult.emit(PurchaseResult.Cancelled)
                }
            }
            else -> {
                val errorMessage = billingResult.debugMessage.ifEmpty { "Store error: ${billingResult.responseCode}" }
                scope.launch {
                    _purchaseResult.emit(PurchaseResult.Error(errorMessage))
                }
            }
        }
    }


    /**
     * Confirms the purchase to Google Play and emits Success so the paywall can close.
     * If not acknowledged within 3 days, Google will refund the user.
     */
    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        if (purchase.isAcknowledged) {
            // Already acked in a previous session — just reflect state and notify UI.
            subscriptionRepository.updateSubscriptionStatus(true)
            _purchaseResult.emit(PurchaseResult.Success)
            return
        }

        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            scope.launch {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Stewardship confirmed: the user is now officially a PRO Steward.
                    subscriptionRepository.updateSubscriptionStatus(true)
                    _purchaseResult.emit(PurchaseResult.Success)
                } else {
                    _purchaseResult.emit(
                        PurchaseResult.Error(
                            billingResult.debugMessage.ifEmpty {
                                "Could not acknowledge purchase"
                            }
                        )
                    )
                }
            }
        }
    }


}
