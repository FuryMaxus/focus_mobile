package com.example.focus.network

import com.example.focus.data.remote.GraphItemDto
import com.example.focus.data.remote.JoinRoomPayload
import com.example.focus.data.remote.LeaderboardItemDto
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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("api/v1/rooms/me/created")
    suspend fun getMyCreatedRooms(): List<RoomResponseDto>

    @GET("api/v1/rooms/me/joined")
    suspend fun getMyJoinedRooms(): List<RoomResponseDto>

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

    @GET("/api/v1/sessions/{room_id}/leaderboard")
    suspend fun getRoomLeaderboard(
        @Path("room_id") roomId: String,
        @Query("limit") limit: Int = 10
    ): List<LeaderboardItemDto>

    @GET("/api/v1/sessions/{room_id}/graph")
    suspend fun getRoomGraph(
        @Path("room_id") roomId: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<GraphItemDto>
}