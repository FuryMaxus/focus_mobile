package com.example.focus.repository

import com.example.focus.data.local.UserPreferences
import com.example.focus.data.remote.LoginPayload
import com.example.focus.data.remote.RegisterPayload
import com.example.focus.network.ApiService
import com.example.focus.network.JwtUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val payload = LoginPayload(email = email, password = password)
            val response = apiService.login(payload)

            if (response.accessToken.isNotEmpty()) {
                userPreferences.saveToken(response.accessToken)
                
                val role = JwtUtils.extractClaim(response.accessToken, "role")
                if (role == null) {
                    userPreferences.clearSession()
                    return Result.failure(Exception("El token devuelto no contiene un rol válido."))
                }
                userPreferences.saveRole(role)

                val stats = apiService.getUserStats()
                userPreferences.saveLevelAndExp(
                    exp = stats.totalExp,
                    level = stats.currentLevel
                )
                Result.success(role)
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