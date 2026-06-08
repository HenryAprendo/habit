package com.henrydev.faithsteward.domain.reminder.usecase

import com.henrydev.faithsteward.domain.reminder.repository.ReminderPreferencesRepository
import javax.inject.Inject

class SetReminderEnabledUseCase @Inject constructor(
    private val repository: ReminderPreferencesRepository,
    private val applyReminderSchedule: ApplyReminderScheduleUseCase
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setEnabled(enabled)
        applyReminderSchedule()
    }
}
