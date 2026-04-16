package com.henrydev.faithsteward

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.henrydev.faithsteward.domain.subscription.usecase.IsProUserUseCase
import com.henrydev.faithsteward.domain.use_cases.CanCreateHabitUseCase
import com.henrydev.faithsteward.ui.theme.HabitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var isProUserUseCase: IsProUserUseCase
    @Inject lateinit var canCreateHabitUseCase: CanCreateHabitUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    HabitApp(
                        isProUserUseCase = isProUserUseCase,
                        canCreateHabitUseCase = canCreateHabitUseCase
                    )
                }
            }
        }
    }
}