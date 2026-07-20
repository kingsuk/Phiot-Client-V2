package com.phiot.phiot_client

import android.app.Application
import com.phiot.phiot_client.data.PhiOTRepository
import com.phiot.phiot_client.data.local.TokenStore
import com.phiot.phiot_client.data.remote.AuthTokenHolder
import com.phiot.phiot_client.data.remote.NetworkModule

class PhiOTApplication : Application() {

    lateinit var repository: PhiOTRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val tokenHolder = AuthTokenHolder()
        repository = PhiOTRepository(
            tokenStore = TokenStore(this),
            tokenHolder = tokenHolder,
            cloudApi = NetworkModule.createCloudApi(tokenHolder),
            deviceApi = NetworkModule.createDeviceApi(),
        )
    }
}
