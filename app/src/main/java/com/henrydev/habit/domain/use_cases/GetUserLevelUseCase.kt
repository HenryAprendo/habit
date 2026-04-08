package com.henrydev.habit.domain.use_cases

import com.henrydev.habit.domain.model.UserStats
import com.henrydev.habit.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.pow

class GetUserLevelUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<UserStats> {
        return userRepository.getUserProfile().map { profile ->
            val xp = profile.totalXp
            val currentLevel = if (xp < 100) 1
                else (xp.toDouble()/100.0).pow(1.0/1.5).toInt() + 1

            val xpForCurrentLevel = if (currentLevel == 1) 0L
                else (100.0 * (currentLevel - 1).toDouble().pow(1.5)).toLong()

            val xpForNextLevel = (100.0 * currentLevel.toDouble().pow(1.5)).toLong()

            val xpInCurrentLevel = xp - xpForCurrentLevel
            val totalXpRequiredForThisLevel = xpForNextLevel - xpForCurrentLevel

            val progress = if (totalXpRequiredForThisLevel > 0) {
                xpInCurrentLevel.toFloat() / totalXpRequiredForThisLevel.toFloat()
            } else 0f

            UserStats(
                totalXp = xp,
                level = currentLevel,
                rankTitle = getRankTitle(currentLevel),
                progressToNextLevel = progress.coerceIn(0f,1f),
                xpRequiredForNext = xpForNextLevel - xp
            )
        }
    }

    private fun getRankTitle(level: Int): String {
        return when {
            level < 5 -> "Novice"
            level < 15 -> "Steady"
            level < 30 -> "Titan"
            level < 50 -> "Master"
            else -> "Legendary"
        }
    }

}

/***
 * 1.
 * XP Base por Nivel: Definiremos que el Nivel 1 requiere 0 XP y el Nivel 2 requiere 100 XP.
 * 2.
 * La Curva de Dificultad: Usaremos la fórmula: XP Total Necesario = 100 * (Nivel ^ 1.5).
 * ◦
 * Nivel 2: 100 XP.
 * ◦
 * Nivel 5: ~1,118 XP.
 * ◦
 * Nivel 10: ~3,162 XP.
 */