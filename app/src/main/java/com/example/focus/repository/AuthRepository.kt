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
                val stats = apiService.getUserStats()
                userPreferences.saveLevelAndExp(
                    exp = stats.totalExp,
                    level = stats.currentLevel
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("El servidor no devolvió un token válido"))
            }
        } catch (e: Exception) {
            userPreferences.clearSession()
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, role: String): Result<String> {
        return try {
            val payload = RegisterPayload(email = email, password = password, role = role)
            val response = apiService.register(payload)
            Result.success(response.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}