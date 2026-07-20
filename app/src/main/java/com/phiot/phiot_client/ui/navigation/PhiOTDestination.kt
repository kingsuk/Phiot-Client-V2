package com.phiot.phiot_client.ui.navigation

import kotlinx.serialization.Serializable

sealed interface PhiOTDestination {
    @Serializable
    data object Login : PhiOTDestination

    @Serializable
    data object Main : PhiOTDestination

    @Serializable
    data class Dataset(
        val deviceId: String,
        val token: String,
    ) : PhiOTDestination
}

enum class MainTab {
    Devices,
    Setup,
}
