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

    suspend fun fetchRoomsCreated(): Result<List<RoomResponseDto>> {
        return try {
            Result.success(apiService.getMyCreatedRooms())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMyJoinedRooms(): Result<List<RoomResponseDto>> {
        return try {
            Result.success(apiService.getMyJoinedRooms())
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

    suspend fun fetchRoomMembers(roomId: String): Result<List<MemberResponseDto>> {
        return try {
            Result.success(apiService.getRoomMembers(roomId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun closeRoom(roomId: String): Result<RoomResponseDto> {
        return try {
            Result.success(apiService.endRoom(roomId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}