package com.henrydev.faithsteward.domain.subscription.model

sealed interface UserStatus {
    data object Free: UserStatus
    data object Pro: UserStatus
}