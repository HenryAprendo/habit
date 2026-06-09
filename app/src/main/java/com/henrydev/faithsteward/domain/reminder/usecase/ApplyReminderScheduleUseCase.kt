package com.henrydev.faithsteward.domain.reminder.usecase

import com.henrydev.faithsteward.domain.reminder.ReminderScheduler
import com.henrydev.faithsteward.domain.reminder.repository.ReminderPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Applies the user's saved reminder preferences to the scheduler:
 * schedules at the configured time when enabled, cancels otherwise.
 * Safe to call on app start and after any preference change.
 */
class ApplyReminderScheduleUseCase @Inject constructor(
    private val repository: ReminderPreferencesRepository,
    private val scheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        val prefs = repository.reminderPreferences.first()
        if (prefs.enabled) {
            scheduler.schedule(prefs.hour, prefs.minute)
        } else {
            scheduler.cancel()
        }
    }
}
