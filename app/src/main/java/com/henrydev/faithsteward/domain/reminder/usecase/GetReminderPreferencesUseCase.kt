package com.henrydev.faithsteward.domain.reminder.usecase

import com.henrydev.faithsteward.domain.reminder.model.ReminderPreferences
import com.henrydev.faithsteward.domain.reminder.repository.ReminderPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderPreferencesUseCase @Inject constructor(
    private val repository: ReminderPreferencesRepository
) {
    operator fun invoke(): Flow<ReminderPreferences> = repository.reminderPreferences
}
