package com.phiot.phiot_client.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phiot.phiot_client.PhiOTApplication
import com.phiot.phiot_client.data.PhiOTRepository

@Composable
fun rememberRepository(): PhiOTRepository {
    val application = LocalContext.current.applicationContext as PhiOTApplication
    return application.repository
}

@Composable
inline fun <reified VM : ViewModel> phiOTViewModel(
    crossinline creator: (PhiOTRepository) -> VM,
): VM {
    val repository = rememberRepository()
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(repository) as T
            }
        },
    )
}
