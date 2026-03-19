package com.henrydev.habit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.henrydev.habit.ui.navigation.HabitNavHost
import com.henrydev.habit.ui.navigation.HabitScreen
import hilt_aggregated_deps._com_henrydev_habit_ui_screen_AddItemViewModel_HiltModules_BindsModule

@Composable
fun HabitApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isBottomBarVisible = HabitScreen.bottomNavItems.any {
        it.route == currentDestination?.route
    }

    Scaffold(
        topBar = {},
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar {
                    HabitScreen.bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = stringResource(screen.title)
                                    )
                                }
                            },
                            label = { Text(text = stringResource(screen.title)) },
                            onClick = {
                                navController.navigate(screen.route) {
                                    //Evita controlar múltiples copias del mismo destino en la pila
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            selected = selected
                        )

                    }
                }
            }
        }
    ) { innerPadding ->
        HabitNavHost(
            controller = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTopAppBar(
    title: Int,
    navigateUp: () -> Unit = { },
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.displaySmall
            )
                },
        navigationIcon = {
           if (canNavigateBack) {
               IconButton(onClick = { navigateUp() }) {
                   Icon(
                       imageVector = Icons.Filled.ArrowBack,
                       contentDescription = "Go to back"
                   )
               }
           }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}