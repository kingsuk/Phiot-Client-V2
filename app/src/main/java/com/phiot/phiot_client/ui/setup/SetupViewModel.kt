package com.phiot.phiot_client.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phiot.phiot_client.data.ApiException
import com.phiot.phiot_client.data.PhiOTRepository
import com.phiot.phiot_client.data.model.WifiNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SetupUiState(
    val networks: List<WifiNetwork> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isConnecting: Boolean = false,
    val selectedNetwork: WifiNetwork? = null,
    val password: String = "",
    val errorMessage: String? = null,
    val statusMessage: String? = null,
)

class SetupViewModel(
    private val repository: PhiOTRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        scanWifi()
    }

    fun refresh() {
        scanWifi(refreshing = true)
    }

    fun selectNetwork(network: WifiNetwork) {
        _uiState.update { it.copy(selectedNetwork = network, password = "", errorMessage = null) }
    }

    fun dismissNetworkDialog() {
        _uiState.update { it.copy(selectedNetwork = null, password = "") }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun connect() {
        val network = _uiState.value.selectedNetwork ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, errorMessage = null) }
            repository.connectWifi(network.ssid, _uiState.value.password)
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isConnecting = false,
                            selectedNetwork = null,
                            password = "",
                            statusMessage = message,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isConnecting = false,
                            errorMessage = (error as? ApiException)?.message ?: "Connection failed.",
                        )
                    }
                }
        }
    }

    private fun scanWifi(refreshing: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !refreshing,
                    isRefreshing = refreshing,
                    errorMessage = null,
                )
            }
            repository.scanWifi()
                .onSuccess { networks ->
                    _uiState.update {
                        it.copy(
                            networks = networks,
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
                            errorMessage = (error as? ApiException)?.message
                                ?: "Failed to scan Wi-Fi. Connect to the device network first.",
                        )
                    }
                }
        }
    }
}
