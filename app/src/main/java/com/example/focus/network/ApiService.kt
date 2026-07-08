package com.example.focus.network

import com.example.focus.data.remote.JoinRoomPayload
import com.example.focus.data.remote.LoginPayload
import com.example.focus.data.remote.MemberResponseDto
import com.example.focus.data.remote.RegisterPayload
import com.example.focus.data.remote.RegisterResponse
import com.example.focus.data.remote.RoomCreatePayload
import com.example.focus.data.remote.RoomResponseDto
import com.example.focus.data.remote.SyncPayload
import com.example.focus.data.remote.SyncResponse
import com.example.focus.data.remote.TokenResponse
import com.example.focus.data.remote.UserStatsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body payload: LoginPayload): TokenResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body payload: RegisterPayload): RegisterResponse

    @POST("api/v1/sync")
    suspend fun syncSessions(@Body payload: SyncPayload): SyncResponse

    @GET("api/v1/users/me/stats")
    suspend fun getUserStats(): UserStatsDto

    @GET("api/v1/rooms/")
    suspend fun getRooms(): List<RoomResponseDto>

    @POST("api/v1/rooms/")
    suspend fun createRoom(@Body payload: RoomCreatePayload): RoomResponseDto

    @POST("api/v1/rooms/join")
    suspend fun joinRoom(@Body payload: JoinRoomPayload): MemberResponseDto

    @GET("api/v1/rooms/{roomId}/members")
    suspend fun getRoomMembers(@retrofit2.http.Path("roomId") roomId: String): List<MemberResponseDto>

    @POST("api/v1/rooms/{roomId}/end")
    suspend fun endRoom(@retrofit2.http.Path("roomId") roomId: String): RoomResponseDto

    @GET("api/v1/sessions/reports")
    suspend fun getRoomStats(@retrofit2.http.Query("room_id") roomId: String): com.example.focus.data.remote.SessionReportResponseDto
}