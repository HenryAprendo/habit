package com.henrydev.habit.domain.billing

sealed interface PurchaseResult {
    data object Success: PurchaseResult
    data object Cancelled: PurchaseResult
    data class Error(val message: String): PurchaseResult
    data object Pending: PurchaseResult
}