package com.phiot.phiot_client.ui.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phiot.phiot_client.R
import com.phiot.phiot_client.data.model.WifiNetwork
import com.phiot.phiot_client.ui.components.HeaderImage
import com.phiot.phiot_client.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    modifier: Modifier = Modifier,
    viewModel: SetupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.statusMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    uiState.selectedNetwork?.let { network ->
        WifiPasswordDialog(
            network = network,
            password = uiState.password,
            isConnecting = uiState.isConnecting,
            onPasswordChange = viewModel::onPasswordChange,
            onDismiss = viewModel::dismissNetworkDialog,
            onConnect = viewModel::connect,
        )
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen(Modifier.padding(padding))
            else -> PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                isRefreshing = uiState.isRefreshing,
                onRefresh = viewModel::refresh,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        HeaderImage(
                            imageRes = R.drawable.drawer_back_image,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            Text(
                                text = "Connect to the device Wi-Fi network, then scan for nearby networks.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = viewModel::refresh) {
                                Text("Scan")
                            }
                        }
                    }
                    items(uiState.networks, key = { it.ssid }) { network ->
                        WifiNetworkCard(
                            network = network,
                            onClick = { viewModel.selectNetwork(network) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WifiNetworkCard(
    network: WifiNetwork,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(network.ssid, style = MaterialTheme.typography.titleMedium)
                Text(
                    network.encryptionType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                "${network.rssi} dBm",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun WifiPasswordDialog(
    network: WifiNetwork,
    password: String,
    isConnecting: Boolean,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConnect: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(network.ssid) },
        text = {
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Wi-Fi password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(enabled = !isConnecting, onClick = onConnect) {
                Text(if (isConnecting) "Connecting…" else "Connect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
