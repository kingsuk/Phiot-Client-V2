package com.phiot.phiot_client.ui.dataset

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phiot.phiot_client.R
import com.phiot.phiot_client.data.model.Dataset
import com.phiot.phiot_client.ui.components.LoadingScreen
import com.phiot.phiot_client.ui.rememberRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatasetScreen(
    deviceId: String,
    deviceToken: String,
    onBack: () -> Unit,
) {
    val repository = rememberRepository()
    val viewModel: DatasetViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DatasetViewModel(repository, deviceId, deviceToken) as T
            }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var datasetToDelete by remember { mutableStateOf<Dataset?>(null) }

    LaunchedEffect(uiState.errorMessage, uiState.statusMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    datasetToDelete?.let { dataset ->
        AlertDialog(
            onDismissRequest = { datasetToDelete = null },
            title = { Text("Delete dataset") },
            text = { Text("Are you sure you want to delete \"${dataset.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDataset(dataset.id)
                        datasetToDelete = null
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { datasetToDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datasets") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
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
                        Image(
                            painter = painterResource(R.drawable.cover2),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    item {
                        uiState.deviceInfo?.let { info ->
                            DeviceInfoHeader(
                                deviceName = info.deviceName,
                                deviceToken = info.deviceToken,
                                deviceTypeId = info.deviceTypeId,
                                callsLeft = uiState.callsLeft,
                            )
                        }
                    }
                    items(uiState.datasets, key = { it.id }) { dataset ->
                        DatasetCard(
                            dataset = dataset,
                            enabled = !uiState.isSending && uiState.callsLeft > 0,
                            onOnClick = { viewModel.sendMessage(dataset.onMessage) },
                            onOffClick = { viewModel.sendMessage(dataset.offMessage) },
                            onDelete = { datasetToDelete = dataset },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoHeader(
    deviceName: String,
    deviceToken: String,
    deviceTypeId: Int,
    callsLeft: Int,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Device Name", style = MaterialTheme.typography.labelMedium)
                Text(deviceName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Device Token", style = MaterialTheme.typography.labelMedium)
                Text(
                    deviceToken,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Image(
                painter = painterResource(
                    if (deviceTypeId == 1) {
                        R.drawable.devicetype1_transparent
                    } else {
                        R.drawable.devicetype2
                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Calls left", style = MaterialTheme.typography.labelMedium)
                Text(
                    callsLeft.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun DatasetCard(
    dataset: Dataset,
    enabled: Boolean,
    onOnClick: () -> Unit,
    onOffClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                dataset.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )
            Button(onClick = onOnClick, enabled = enabled) {
                Text("On")
            }
            Spacer(modifier = Modifier.size(8.dp))
            Button(onClick = onOffClick, enabled = enabled) {
                Text("Off")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete dataset")
            }
        }
    }
}
