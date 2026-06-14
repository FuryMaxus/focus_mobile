package com.example.focus.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// --- Modelos para Login ---
data class LoginPayload(
    val email: String,
    val password: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String
)

// --- Modelos para Registro ---
data class RegisterPayload(
    val email: String,
    val password: String,
    val role: String = "student"
)

data class RegisterResponse(
    val id: String,
    val email: String
)

// --- Modelos para Sincronización de Stats ---
data class SessionItem(
    val activity_type: String,
    val start_time: String,
    val end_time: String,
    val room_id: String? = null
)

data class SyncPayload(
    val sessions: List<SessionItem>
)

data class RewardItem(
    val id: String,
    val name: String
)

data class SyncResponse(
    val status: String,
    val processed_sessions_count: Int,
    val total_exp_gained: Int,
    val current_level: Int,
    val leveled_up: Boolean,
    val levels_gained: Int,
    val rewards: List<RewardItem>
)

// --- Interfaz de Endpoints ---
interface ApiService {

    // Rutas de autenticación apuntando al BFF
    @POST("api/v1/auth/login")
    suspend fun login(@Body payload: LoginPayload): TokenResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body payload: RegisterPayload): RegisterResponse

    // Ruta de sincronización de XP apuntando al BFF
    @POST("api/v1/sync")
    suspend fun syncSessions(@Body payload: SyncPayload): Response<SyncResponse>
}