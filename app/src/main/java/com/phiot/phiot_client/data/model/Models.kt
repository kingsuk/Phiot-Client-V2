package com.phiot.phiot_client.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val email: String,
)

@Serializable
data class Device(
    val id: String,
    @SerialName("deviceName") val deviceName: String,
    @SerialName("device_token") val deviceToken: String,
    @SerialName("device_type_id") val deviceTypeId: Int,
)

@Serializable
data class DeviceInfo(
    @SerialName("deviceName") val deviceName: String,
    @SerialName("device_token") val deviceToken: String,
    @SerialName("apiCallsPerDay") val apiCallsPerDay: Int,
    @SerialName("logCountToday") val logCountToday: Int,
    @SerialName("device_type_id") val deviceTypeId: Int,
)

@Serializable
data class Dataset(
    @SerialName("ds_id") val id: String,
    @SerialName("ds_name") val name: String,
    @SerialName("jsonData") val onMessage: String,
    @SerialName("reverseJsonData") val offMessage: String,
)

@Serializable
data class StatusMessage(
    @SerialName("statusMessage") val message: String,
)

@Serializable
data class WifiNetwork(
    val ssid: String,
    val encryptionType: String,
    val rssi: String,
)

@Serializable
data class ValidationErrorResponse(
    val errors: Map<String, List<String>> = emptyMap(),
)
