package com.phiot.phiot_client.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phiot.phiot_client.data.PhiOTRepository
import com.phiot.phiot_client.data.local.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repository: PhiOTRepository,
) : ViewModel() {

    val session: StateFlow<UserSession?> = repository.session
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun bootstrap() {
        if (_isReady.value) return
        viewModelScope.launch {
            repository.restoreSession()
            _isReady.value = true
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onComplete()
        }
    }
}
