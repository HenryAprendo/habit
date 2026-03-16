package com.henrydev.habit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.henrydev.habit.ui.home.HomeScreen
import com.henrydev.habit.ui.screen.AddItemScreen

@Composable
fun HabitNavHost(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = controller,
        startDestination = HabitScreen.Home.route,
        modifier = modifier
    ) {
        composable(route = HabitScreen.Home.route) {
            HomeScreen(
                onNavigateToAddHabit = {
                    controller.navigate(HabitScreen.AddHabit.route)
                }
            )
        }
        composable(route = HabitScreen.AddHabit.route) {
            AddItemScreen(
                onNavigateBack = { controller.popBackStack() },
                onNavigateUp = { controller.navigateUp() }
            )
        }
    }
}