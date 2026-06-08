package com.henrydev.faithsteward

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.henrydev.faithsteward.domain.subscription.usecase.IsProUserUseCase
import com.henrydev.faithsteward.domain.use_cases.CanCreateHabitUseCase
import com.henrydev.faithsteward.ui.navigation.HabitNavHost
import com.henrydev.faithsteward.ui.navigation.HabitScreen
import com.henrydev.faithsteward.ui.screen.onboarding.OnboardingScreen
import com.henrydev.faithsteward.ui.screen.onboarding.OnboardingViewModel


@Composable
fun HabitApp(
    isProUserUseCase: IsProUserUseCase,
    canCreateHabitUseCase: CanCreateHabitUseCase,
    modifier: Modifier = Modifier
) {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingCompleted by onboardingViewModel.completed.collectAsStateWithLifecycle()

    when (onboardingCompleted) {
        // null = flag still loading; keep a blank surface to avoid flashing onboarding.
        null -> Box(modifier = Modifier.fillMaxSize())
        false -> OnboardingScreen(onFinish = { onboardingViewModel.complete() })
        else -> MainApp(
            isProUserUseCase = isProUserUseCase,
            canCreateHabitUseCase = canCreateHabitUseCase,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainApp(
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
    var showLimitDialog by remember { mutableStateOf(false) }

    if (showLimitDialog) {
        AlertDialog(
            onDismissRequest = { showLimitDialog = false },
            title = { Text(stringResource(R.string.habit_limit_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.habit_limit_message,
                        CanCreateHabitUseCase.FREE_HABIT_LIMIT
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLimitDialog = false
                    navController.navigate(HabitScreen.Paywall.route)
                }) {
                    Text(stringResource(R.string.habit_limit_cta))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLimitDialog = false }) {
                    Text(stringResource(R.string.habit_limit_dismiss))
                }
            }
        )
    }

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
                            showLimitDialog = true
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
                        contentDescription = stringResource(R.string.add_habit_title),
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
                       contentDescription = stringResource(R.string.add_habit_back_desc)
                   )
               }
           }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}
