package com.example.focus.network

import com.example.focus.data.local.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authManager: AuthManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = chain.request().newBuilder()

        val token = runBlocking { userPreferences.getToken.firstOrNull() }

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        val isAuthRoute = request.url.encodedPath.contains("login") ||
                request.url.encodedPath.contains("register")

        if (response.code == 401 && !isAuthRoute) {
            runBlocking {
                authManager.triggerSessionExpired()
            }
        }
        return response
    }
}