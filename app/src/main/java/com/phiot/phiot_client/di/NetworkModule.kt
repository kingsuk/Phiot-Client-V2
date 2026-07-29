package com.phiot.phiot_client.di

import android.content.Context
import com.phiot.phiot_client.AppConfig
import com.phiot.phiot_client.BuildConfig
import com.phiot.phiot_client.data.local.TokenStore
import com.phiot.phiot_client.data.remote.AuthInterceptor
import com.phiot.phiot_client.data.remote.AuthTokenHolder
import com.phiot.phiot_client.data.remote.PhiOTCloudApi
import com.phiot.phiot_client.data.remote.PhiOTDeviceApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CloudClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideAuthTokenHolder(): AuthTokenHolder = AuthTokenHolder()

    @Provides
    @Singleton
    fun provideTokenStore(@ApplicationContext context: Context): TokenStore = TokenStore(context)

    @Provides
    @Singleton
    @CloudClient
    fun provideCloudOkHttpClient(
        authTokenHolder: AuthTokenHolder,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(authTokenHolder))

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                },
            )
        }

        return builder.build()
    }

    @Provides
    @Singleton
    @DeviceClient
    fun provideDeviceOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideCloudApi(
        @CloudClient client: OkHttpClient,
        json: Json,
    ): PhiOTCloudApi =
        Retrofit.Builder()
            .baseUrl(AppConfig.CLOUD_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()

    @Provides
    @Singleton
    fun provideDeviceApi(
        @DeviceClient client: OkHttpClient,
        json: Json,
    ): PhiOTDeviceApi =
        Retrofit.Builder()
            .baseUrl(AppConfig.DEVICE_BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
}
