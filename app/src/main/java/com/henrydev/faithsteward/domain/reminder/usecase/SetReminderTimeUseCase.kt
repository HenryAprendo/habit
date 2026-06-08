package com.henrydev.faithsteward.domain.reminder.usecase

import com.henrydev.faithsteward.domain.reminder.repository.ReminderPreferencesRepository
import javax.inject.Inject

class SetReminderTimeUseCase @Inject constructor(
    private val repository: ReminderPreferencesRepository,
    private val applyReminderSchedule: ApplyReminderScheduleUseCase
) {
    suspend operator fun invoke(hour: Int, minute: Int) {
        repository.setTime(hour, minute)
        applyReminderSchedule()
    }
}
