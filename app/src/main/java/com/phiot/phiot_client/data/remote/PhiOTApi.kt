package com.phiot.phiot_client.data.remote

import com.phiot.phiot_client.data.model.AuthResponse
import com.phiot.phiot_client.data.model.Dataset
import com.phiot.phiot_client.data.model.Device
import com.phiot.phiot_client.data.model.DeviceInfo
import com.phiot.phiot_client.data.model.StatusMessage
import com.phiot.phiot_client.data.model.WifiNetwork
import retrofit2.http.GET
import retrofit2.http.Query

interface PhiOTCloudApi {
    @GET("auth/AuthAttempt")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String,
    ): AuthResponse

    @GET("device/GetAllDevicesByUser")
    suspend fun getDevices(): List<Device>

    @GET("device/DeleteDeviceByDeviceAndUserId")
    suspend fun deleteDevice(@Query("id") deviceId: String): StatusMessage

    @GET("device/GetDeviceInfoByDeviceId")
    suspend fun getDeviceInfo(@Query("deviceId") deviceId: String): DeviceInfo

    @GET("Dataset/GetAllDatasetByUserIdAndDeviceId")
    suspend fun getDatasets(@Query("ds_deviceId") deviceId: String): List<Dataset>

    @GET("Dataset/DeleteDatasetByDsIdAndUserId")
    suspend fun deleteDataset(@Query("ds_id") datasetId: String): StatusMessage

    @GET("publish/sendToDevice")
    suspend fun sendToDevice(
        @Query("token") token: String,
        @Query("message") message: String,
    ): StatusMessage
}

interface PhiOTDeviceApi {
    @GET("wifiscan")
    suspend fun scanWifi(): List<WifiNetwork>

    @GET("wificonnect")
    suspend fun connectWifi(
        @Query("ssid") ssid: String,
        @Query("password") password: String,
    ): String
}
