package com.henrydev.habit.ui.screen.settings

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrydev.habit.domain.subscription.model.UserStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val exportStatus by viewModel.exportStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Local Scroll Behavior for collapsing TopBar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            val status = exportStatus
            if (status is ExportStatus.Success) {
                val json = status.jsonData
                saveJsonToUri(context,it,json)
                viewModel.resetExportStatus()
            }
        }
    }

    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { resource ->
            val jsonString = readJsonFromUri(context,resource)
            if (jsonString != null) {
                viewModel.startImport(jsonString)
            }
        }
    }

    LaunchedEffect(exportStatus) {
        if (exportStatus is ExportStatus.Success) {
            createFileLauncher.launch("habit_backup_${System.currentTimeMillis()}.json")
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "App Stewardship", // Instead of Settings
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            SubscriptionStatusCard(
                status = uiState.userStatus,
                onUpgradeClick = onNavigateToPaywall
            )

            Spacer(modifier = Modifier.height(24.dp))
            ExportSection(
                isLoading = exportStatus is ExportStatus.Loading,
                onExportClick = { viewModel.startExport() },
                onImportClick = { openFileLauncher.launch("application/json") }
            )
        }
    }

}

private fun saveJsonToUri(context: Context, uri: Uri, json: String) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(json.toByteArray())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun readJsonFromUri(
    context: Context,
    uri: Uri
): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { it.readText() }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
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
                    imageVector = if (isPro) Icons.Default.Star else Icons.Default.CardMembership,
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

@Composable
fun ExportSection(
    isLoading: Boolean,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Data Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Backup & Restore",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Export your habits and history to a JSON file to keep them safe or move them to another device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onExportClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Export to JSON")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                OutlinedButton(
                    onClick = onImportClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Import from JSON")
                }
            }
        }
    }
}
