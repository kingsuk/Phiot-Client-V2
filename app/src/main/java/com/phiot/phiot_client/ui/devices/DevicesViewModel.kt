package com.phiot.phiot_client.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phiot.phiot_client.data.ApiException
import com.phiot.phiot_client.data.PhiOTRepository
import com.phiot.phiot_client.data.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DevicesUiState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null,
)

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val repository: PhiOTRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState())
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init {
        loadDevices()
    }

    fun refresh() {
        loadDevices(refreshing = true)
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            repository.deleteDevice(deviceId)
                .onSuccess { response ->
                    _uiState.update { it.copy(statusMessage = response.message) }
                    loadDevices()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = (error as? ApiException)?.message ?: "Delete failed.")
                    }
                }
        }
    }

    private fun loadDevices(refreshing: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !refreshing,
                    isRefreshing = refreshing,
                    errorMessage = null,
                )
            }
            repository.getDevices()
                .onSuccess { devices ->
                    _uiState.update {
                        it.copy(
                            devices = devices,
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = (error as? ApiException)?.message ?: "Failed to load devices.",
                        )
                    }
                }
        }
    }
}
