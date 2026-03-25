package com.henrydev.habit.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.domain.subscription.model.UserStatus

@Composable
fun SettingsScreen(
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        SubscriptionStatusCard(
            status = uiState.userStatus,
            onUpgradeClick = onNavigateToPaywall
        )
    }

}


@Composable
fun SubscriptionStatusCard(
    status: UserStatus,
    onUpgradeClick: () -> Unit,
) {
    val isPro = status is UserStatus.Pro

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isPro)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (isPro) Icons.Default.CardMembership else Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isPro)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Subscription Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (isPro)
                    "You are a Premium Member. Enjoy all unlimited features!"
                else
                    "You are currently on the Free Plan. Upgrade to unlock all features.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!isPro) {
                Button(
                    onClick = onUpgradeClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Upgrade to Pro")
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Pro",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

        }
    }



}




















