package com.henrydev.habit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.henrydev.habit.ui.navigation.HabitNavHost

@Composable
fun HabitApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    HabitNavHost(controller = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTopAppBar(
    title: Int,
    navigateUp: () -> Unit = { },
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(title)) },
        navigationIcon = {
           if (canNavigateBack) {
               IconButton(onClick = { navigateUp() }) {
                   Icon(
                       imageVector = Icons.Filled.ArrowBack,
                       contentDescription = "Go to back"
                   )
               }
           }
        }
    )
}