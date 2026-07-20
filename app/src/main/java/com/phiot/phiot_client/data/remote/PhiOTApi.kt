package com.phiot.phiot_client.data.remote

import com.phiot.phiot_client.AppConfig
import com.phiot.phiot_client.data.model.AuthResponse
import com.phiot.phiot_client.data.model.Dataset
import com.phiot.phiot_client.data.model.Device
import com.phiot.phiot_client.data.model.DeviceInfo
import com.phiot.phiot_client.data.model.StatusMessage
import com.phiot.phiot_client.data.model.WifiNetwork
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.create
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

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

object NetworkModule {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun createCloudApi(tokenHolder: AuthTokenHolder): PhiOTCloudApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(tokenHolder))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                },
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(AppConfig.CLOUD_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }

    fun createDeviceApi(): PhiOTDeviceApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(AppConfig.DEVICE_BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }
}
