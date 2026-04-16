package com.henrydev.faithsteward.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.henrydev.faithsteward.domain.subscription.usecase.IsProUserUseCase
import com.henrydev.faithsteward.ui.screen.home.HomeScreen
import com.henrydev.faithsteward.ui.screen.add_habit.AddItemScreen
import com.henrydev.faithsteward.ui.screen.challenges.ChallengesScreen
import com.henrydev.faithsteward.ui.screen.progress.ProgressScreen
import com.henrydev.faithsteward.ui.screen.settings.PaywallScreen
import com.henrydev.faithsteward.ui.screen.settings.SettingsScreen

@Composable
fun HabitNavHost(
    controller: NavHostController,
    isProUserUseCase: IsProUserUseCase,
    modifier: Modifier = Modifier
) {

    val isPro by isProUserUseCase().collectAsStateWithLifecycle(false)

    NavHost(
        navController = controller,
        startDestination = HabitScreen.Home.route,
        modifier = modifier
    ) {
        composable(route = HabitScreen.Home.route) {
            HomeScreen(
                onNavigateToPaywall = { controller.navigate(HabitScreen.Paywall.route)}
            )
        }

        composable(route = HabitScreen.Progress.route) {
            ProgressScreen(
                onNavigateToPaywall = {
                    controller.navigate(HabitScreen.Paywall.route)
                }
            )
        }

        composable(route = HabitScreen.Challenges.route) {
            ChallengesScreen(
                onNavigateToPaywall = {
                    controller.navigate(HabitScreen.Paywall.route)
                }
            )
        }

        composable(route = HabitScreen.Settings.route) {
            SettingsScreen(
                onNavigateToPaywall = {
                    controller.navigate(HabitScreen.Paywall.route)
                }
            )
        }

        composable(route = HabitScreen.AddHabit.route) {
            AddItemScreen(
                onNavigateBack = { controller.popBackStack() },
            )
        }
        composable(route = HabitScreen.Paywall.route) {
            PaywallScreen(onDismiss = { controller.popBackStack() })
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
