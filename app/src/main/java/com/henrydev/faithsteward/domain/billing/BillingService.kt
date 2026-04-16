package com.henrydev.faithsteward.domain.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface BillingService {
    val isReady: StateFlow<Boolean>
    fun connect()
    suspend fun purchaseProduct(activity: Activity, productId: String): PurchaseResult
    suspend fun checkSubscriptionStatus(): Boolean
    suspend fun getProductPrice(productId: String): String?
    fun endConnection()
}