package com.henrydev.habit.domain.util

import java.util.Calendar

fun Long.toStartOfDay(): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toStartOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}