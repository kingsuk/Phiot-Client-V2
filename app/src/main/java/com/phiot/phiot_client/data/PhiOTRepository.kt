package com.phiot.phiot_client.data

import com.phiot.phiot_client.data.local.TokenStore
import com.phiot.phiot_client.data.local.UserSession
import com.phiot.phiot_client.data.model.AuthResponse
import com.phiot.phiot_client.data.model.Dataset
import com.phiot.phiot_client.data.model.Device
import com.phiot.phiot_client.data.model.DeviceInfo
import com.phiot.phiot_client.data.model.StatusMessage
import com.phiot.phiot_client.data.model.WifiNetwork
import com.phiot.phiot_client.data.remote.AuthTokenHolder
import com.phiot.phiot_client.data.remote.PhiOTCloudApi
import com.phiot.phiot_client.data.remote.PhiOTDeviceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhiOTRepository @Inject constructor(
    private val tokenStore: TokenStore,
    private val tokenHolder: AuthTokenHolder,
    private val cloudApi: PhiOTCloudApi,
    private val deviceApi: PhiOTDeviceApi,
) {
    val session: Flow<UserSession?> = tokenStore.session

    suspend fun restoreSession() {
        val current = tokenStore.session.first()
        tokenHolder.token = current?.token.orEmpty()
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        apiCall {
            cloudApi.login(email = email, password = password).also { response ->
                tokenHolder.token = response.token
                tokenStore.saveSession(response.token, response.email)
            }
        }

    suspend fun logout() {
        tokenHolder.token = ""
        tokenStore.clearSession()
    }

    suspend fun getDevices(): Result<List<Device>> = apiCall { cloudApi.getDevices() }

    suspend fun deleteDevice(deviceId: String): Result<StatusMessage> =
        apiCall { cloudApi.deleteDevice(deviceId) }

    suspend fun getDeviceInfo(deviceId: String): Result<DeviceInfo> =
        apiCall { cloudApi.getDeviceInfo(deviceId) }

    suspend fun getDatasets(deviceId: String): Result<List<Dataset>> =
        apiCall { cloudApi.getDatasets(deviceId) }

    suspend fun deleteDataset(datasetId: String): Result<StatusMessage> =
        apiCall { cloudApi.deleteDataset(datasetId) }

    suspend fun sendToDevice(token: String, message: String): Result<StatusMessage> =
        apiCall { cloudApi.sendToDevice(token, message) }

    suspend fun scanWifi(): Result<List<WifiNetwork>> = apiCall { deviceApi.scanWifi() }

    suspend fun connectWifi(ssid: String, password: String): Result<String> =
        apiCall { deviceApi.connectWifi(ssid, password) }

    private suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (error: HttpException) {
        Result.failure(ApiException(parseHttpError(error), error.code()))
    } catch (error: IOException) {
        Result.failure(ApiException("Network error. Please check your connection.", null))
    } catch (error: Exception) {
        Result.failure(error)
    }

    private fun parseHttpError(error: HttpException): String {
        val body = error.response()?.errorBody()?.string().orEmpty()
        if (body.isBlank()) return "Request failed (${error.code()})"

        return runCatching {
            val json = Json { ignoreUnknownKeys = true }
            val validation = json.decodeFromString<Map<String, List<String>>>(body)
            validation.values.flatten().firstOrNull()
        }.getOrNull() ?: body
    }
}

class ApiException(
    override val message: String,
    val code: Int?,
) : Exception(message)
