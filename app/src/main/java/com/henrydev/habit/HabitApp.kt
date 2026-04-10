package com.henrydev.habit

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.henrydev.habit.domain.subscription.usecase.IsProUserUseCase
import com.henrydev.habit.domain.use_cases.CanCreateHabitUseCase
import com.henrydev.habit.ui.navigation.HabitNavHost
import com.henrydev.habit.ui.navigation.HabitScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitApp(
    isProUserUseCase: IsProUserUseCase,
    canCreateHabitUseCase: CanCreateHabitUseCase,
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isBottomBarVisible =  HabitScreen.bottomNavItems.any {
        it.route == currentDestination?.route
    }

    val canCreateHabit by canCreateHabitUseCase().collectAsStateWithLifecycle(true)

    Scaffold(
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
        },
        floatingActionButton = {
            if (currentDestination?.route == HabitScreen.Home.route) {
                FloatingActionButton(
                    onClick = {
                        if (canCreateHabit) {
                            navController.navigate(HabitScreen.AddHabit.route)
                        } else {
                            navController.navigate(HabitScreen.Paywall.route)
                        }
                    },
                    shape = CutCornerShape(topStart = 15.dp, bottomEnd = 15.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add habit",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        modifier = modifier
        //modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        HabitNavHost(
            controller = navController,
            isProUserUseCase = isProUserUseCase,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
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