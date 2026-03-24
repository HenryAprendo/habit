package com.henrydev.habit.domain.subscription.model

sealed interface UserStatus {
    data object Free: UserStatus
    data object Pro: UserStatus
}