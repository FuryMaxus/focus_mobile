package com.example.focus.network

import com.example.focus.data.remote.LoginPayload
import com.example.focus.data.remote.RegisterPayload
import com.example.focus.data.remote.RegisterResponse
import com.example.focus.data.remote.SyncPayload
import com.example.focus.data.remote.SyncResponse
import com.example.focus.data.remote.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body payload: LoginPayload): TokenResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body payload: RegisterPayload): RegisterResponse

    @POST("api/v1/sync")
    suspend fun syncSessions(@Body payload: SyncPayload): Response<SyncResponse>
}