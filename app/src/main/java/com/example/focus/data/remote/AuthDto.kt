package com.example.focus.data.remote

data class LoginPayload(
    val email: String,
    val password: String
)

data class TokenResponse(
    val accessToken: String,
    val tokenType: String
)

data class RegisterPayload(
    val email: String,
    val password: String,
    val role: String = "student"
)

data class RegisterResponse(
    val id: String,
    val email: String
)