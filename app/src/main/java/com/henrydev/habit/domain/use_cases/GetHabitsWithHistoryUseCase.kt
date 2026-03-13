package com.henrydev.habit.domain.use_cases

import com.henrydev.habit.domain.model.HabitWithHistory
import com.henrydev.habit.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsWithHistoryUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<List<HabitWithHistory>> {
        return repository.getHabitsWithHistory()
    }
}