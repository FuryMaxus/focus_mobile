package com.example.focus.repository

import com.example.focus.data.local.UserPreferences
import com.example.focus.data.remote.LoginPayload
import com.example.focus.data.remote.RegisterPayload
import com.example.focus.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val payload = LoginPayload(email = email, password = password)
            val response = apiService.login(payload)

            if (response.accessToken.isNotEmpty()) {
                userPreferences.saveToken(response.accessToken)
                Result.success(Unit)
            } else {
                Result.failure(Exception("El servidor no devolvió un token válido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<String> {
        return try {
            val payload = RegisterPayload(email = email, password = password)
            val response = apiService.register(payload)
            Result.success(response.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}