package com.phiot.phiot_client.ui.dataset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phiot.phiot_client.data.ApiException
import com.phiot.phiot_client.data.PhiOTRepository
import com.phiot.phiot_client.data.model.Dataset
import com.phiot.phiot_client.data.model.DeviceInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DatasetUiState(
    val deviceInfo: DeviceInfo? = null,
    val datasets: List<Dataset> = emptyList(),
    val callsLeft: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null,
)

@HiltViewModel(assistedFactory = DatasetViewModel.Factory::class)
class DatasetViewModel @AssistedInject constructor(
    private val repository: PhiOTRepository,
    @Assisted("deviceId") private val deviceId: String,
    @Assisted("deviceToken") private val deviceToken: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("deviceId") deviceId: String,
            @Assisted("deviceToken") deviceToken: String,
        ): DatasetViewModel
    }

    private val _uiState = MutableStateFlow(DatasetUiState())
    val uiState: StateFlow<DatasetUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() {
        load(refreshing = true)
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun sendMessage(message: String) {
        if (_uiState.value.callsLeft <= 0) {
            _uiState.update { it.copy(errorMessage = "No more calls left for today.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, errorMessage = null) }
            repository.sendToDevice(deviceToken, message)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            callsLeft = (it.callsLeft - 1).coerceAtLeast(0),
                            statusMessage = response.message,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = (error as? ApiException)?.message ?: "Failed to send command.",
                        )
                    }
                }
        }
    }

    fun deleteDataset(datasetId: String) {
        viewModelScope.launch {
            repository.deleteDataset(datasetId)
                .onSuccess { response ->
                    _uiState.update { it.copy(statusMessage = response.message) }
                    load()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = (error as? ApiException)?.message ?: "Delete failed.")
                    }
                }
        }
    }

    private fun load(refreshing: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !refreshing,
                    isRefreshing = refreshing,
                    errorMessage = null,
                )
            }

            val infoResult = repository.getDeviceInfo(deviceId)
            val datasetsResult = repository.getDatasets(deviceId)

            if (infoResult.isSuccess && datasetsResult.isSuccess) {
                val info = infoResult.getOrThrow()
                _uiState.update {
                    it.copy(
                        deviceInfo = info,
                        datasets = datasetsResult.getOrThrow(),
                        callsLeft = info.apiCallsPerDay - info.logCountToday,
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
            } else {
                val error = infoResult.exceptionOrNull() ?: datasetsResult.exceptionOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = (error as? ApiException)?.message ?: "Failed to load device data.",
                    )
                }
            }
        }
    }
}
