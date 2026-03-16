package com.henrydev.habit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.henrydev.habit.ui.home.HomeScreen
import com.henrydev.habit.ui.screen.AddItemScreen

@Composable
fun HabitApp(
    modifier: Modifier = Modifier
) {
    AddItemScreen()
}