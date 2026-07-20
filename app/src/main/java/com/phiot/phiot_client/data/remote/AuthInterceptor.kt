package com.phiot.phiot_client.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenHolder: AuthTokenHolder,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (tokenHolder.token.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer ${tokenHolder.token}")
        }
        return chain.proceed(requestBuilder.build())
    }
}
