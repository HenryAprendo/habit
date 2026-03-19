package com.henrydev.habit.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.henrydev.habit.ui.screen.home.HomeScreen
import com.henrydev.habit.ui.screen.add_habit.AddItemScreen

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

        composable(route = HabitScreen.Progress.route) {
            PlaceholderScreen(HabitScreen.Progress.title)
        }

        composable(route = HabitScreen.Challenges.route) {
            PlaceholderScreen(HabitScreen.Challenges.title)
        }

        composable(route = HabitScreen.Settings.route) {
            PlaceholderScreen(HabitScreen.Settings.title)
        }

        composable(route = HabitScreen.AddHabit.route) {
            AddItemScreen(
                onNavigateBack = { controller.popBackStack() },
                onNavigateUp = { controller.navigateUp() }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Coming Soon: ${stringResource(title)}",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}














