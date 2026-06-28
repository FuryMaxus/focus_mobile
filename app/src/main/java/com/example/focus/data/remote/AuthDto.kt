package com.example.focus.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginPayload(
    val email: String,
    val password: String
)
@Serializable
data class TokenResponse(
    val accessToken: String,
    val tokenType: String
)
@Serializable
data class RegisterPayload(
    val email: String,
    val password: String,
    val role: String = "student"
)
@Serializable
data class RegisterResponse(
    val id: String,
    val email: String
)