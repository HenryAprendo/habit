package com.henrydev.faithsteward

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.henrydev.faithsteward.domain.subscription.usecase.IsProUserUseCase
import com.henrydev.faithsteward.domain.use_cases.CanCreateHabitUseCase
import com.henrydev.faithsteward.ui.components.NotificationRationaleDialog
import com.henrydev.faithsteward.ui.notifications.NotificationScheduler
import com.henrydev.faithsteward.ui.theme.HabitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var isProUserUseCase: IsProUserUseCase
    @Inject lateinit var canCreateHabitUseCase: CanCreateHabitUseCase

    @Inject lateinit var notificationScheduler: NotificationScheduler

    private var showRationale by mutableStateOf(false)

    // 1. Definition of the Permission Launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Stewardship confirmed: Schedule the reminder at 13:00
            notificationScheduler.scheduleDailyReminder()
        }
    }

    /**
     * Professional check for notification permissions (Android 13+).
     * Decouples the UI logic from the OS requirements.
     */
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Already have stewardship authority
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showRationale = true
                }
                else -> {
                    // Direct request for the first time
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            notificationScheduler.scheduleDailyReminder()
        }

        checkAndRequestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            HabitTheme {
                if (showRationale) {
                    NotificationRationaleDialog(
                        onConfirm = {
                            showRationale = false
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        onDismiss = { showRationale = false }
                    )
                }
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