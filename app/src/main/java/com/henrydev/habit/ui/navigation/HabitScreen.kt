package com.henrydev.habit.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.henrydev.habit.R

sealed class HabitScreen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector? = null
) {

    data object Home: HabitScreen(
        route = "home_screen",
        title = R.string.home_title,
        icon = Icons.Filled.Home
    )

    data object Progress: HabitScreen(
        route = "progress_screen",
        title = R.string.progress_title,
        icon = Icons.Filled.BarChart
    )

    data object Challenges: HabitScreen(
        route = "challenges_screen",
        title = R.string.challenges_title,
        icon = Icons.Filled.EmojiEvents
    )

    data object Settings: HabitScreen(
        route = "settings_screen",
        title = R.string.settings_title,
        icon = Icons.Filled.Settings
    )

    data object AddHabit: HabitScreen(
        route = "add_habit_screen",
        title = R.string.add_habit_title
    )

    data object Edithabit: HabitScreen(
        route = "edit_habit_screen/{habitId}",
        title = R.string.edit_habit_title
    ) {
        fun createRoute(habitId: Int): String = "edit_habit_screen/$habitId"
        const val itemIdArg = "habitId"
    }

    data object Paywall: HabitScreen(
        route = "paywall_screen",
        title = R.string.paywall_title
    )

    companion object {
        val bottomNavItems = listOf(HabitScreen.Home, Progress, Challenges, HabitScreen.Settings)
    }

}