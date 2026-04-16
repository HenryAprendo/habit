package com.henrydev.faithsteward.domain.use_cases

import com.henrydev.faithsteward.domain.model.Habit
import com.henrydev.faithsteward.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        repository.deleteHabit(habit)
    }
}