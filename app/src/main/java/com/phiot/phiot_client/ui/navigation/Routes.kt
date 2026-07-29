package com.phiot.phiot_client.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object MainRoute

@Serializable
data class DatasetRoute(
    val deviceId: String,
    val token: String,
)

enum class MainTab {
    Devices,
    Setup,
}
