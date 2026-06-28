package com.example.focus.repository

import com.example.focus.data.remote.JoinRoomPayload
import com.example.focus.data.remote.MemberResponseDto
import com.example.focus.data.remote.RoomCreatePayload
import com.example.focus.data.remote.RoomResponseDto
import com.example.focus.network.ApiService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun fetchRooms(): Result<List<RoomResponseDto>> {
        return try {
            Result.success(apiService.getRooms())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRoom(payload: RoomCreatePayload): Result<RoomResponseDto> {
        return try {
            Result.success(apiService.createRoom(payload))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinRoom(invitationCode: String): Result<MemberResponseDto> {
        return try {
            Result.success(apiService.joinRoom(JoinRoomPayload(invitationCode)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}