package com.henrydev.faithsteward.ui.screen.settings

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.faithsteward.R

@Composable
fun PaywallScreen(
    onDismiss: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onDismiss()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Icono con Identidad: Crecimiento espiritual
        Icon(
            imageVector = Icons.Default.AutoGraph,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.paywall_commit_spiritual_growth),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.headlineMedium.lineHeight * 1.2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.paywall_invest_stewardship),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Lista de Beneficios Reales del MVP
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PremiumFeatureItem(
                title = stringResource(R.string.paywall_feature_unlimited_title),
                description = stringResource(R.string.paywall_feature_unlimited_desc)
            )
            PremiumFeatureItem(
                title = stringResource(R.string.paywall_feature_analysis_title),
                description = stringResource(R.string.paywall_feature_analysis_desc)
            )
            PremiumFeatureItem(
                title = stringResource(R.string.paywall_feature_challenges_title),
                description = stringResource(R.string.paywall_feature_challenges_desc)
            )
            PremiumFeatureItem(
                title = stringResource(R.string.paywall_feature_streak_title),
                description = stringResource(R.string.paywall_feature_streak_desc)
            )
            PremiumFeatureItem(
                title = stringResource(R.string.paywall_feature_no_ads_title),
                description = stringResource(R.string.paywall_feature_no_ads_desc)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // SECCIÓN DE PLANES: Selección de Mayordomía
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PricingCard(
                title = stringResource(R.string.paywall_plan_annual_title),
                price = stringResource(R.string.paywall_plan_annual_price),
                description = stringResource(R.string.paywall_plan_annual_desc),
                trialPeriod = stringResource(R.string.paywall_plan_annual_trial),
                isHighlighted = true,
                enabled = !uiState.isLoading,
                onClick = { viewModel.purchasePro(activity,PaywallViewModel.PRODUCT_ID_ANNUAL) } // Aquí pasarás el ID del plan anual
            )

            PricingCard(
                title = stringResource(R.string.paywall_plan_monthly_title),
                price = stringResource(R.string.paywall_plan_monthly_price),
                description = stringResource(R.string.paywall_plan_monthly_desc),
                trialPeriod = null,
                isHighlighted = false,
                enabled = !uiState.isLoading,
                onClick = { viewModel.purchasePro(activity,PaywallViewModel.PRODUCT_ID_MONTHLY) } // Aquí pasarás el ID del plan mensual
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }

        if (uiState.isPendingPayment) {
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.paywall_payment_pending),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        uiState.errorMessage?.let { error ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            TextButton(
                onClick = { viewModel.restorePurchases() }
            ) {
                Text(
                    text = stringResource(R.string.paywall_restore_purchases),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = stringResource(R.string.paywall_continue_basic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = stringResource(R.string.paywall_trial_disclaimer),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,    description: String,
    trialPeriod: String? = null, // "7 Days Free"
    isHighlighted: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        onClick = if (enabled) onClick else ({}),
        shape = MaterialTheme.shapes.large,
        color = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isHighlighted) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (trialPeriod != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = trialPeriod.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(text = price, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun PremiumFeatureItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
