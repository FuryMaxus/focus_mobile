package com.example.focus.data.remote
import kotlinx.serialization.Serializable

//TODO: change parameters to reflect the final ones on the backend

@Serializable
data class RoomCreatePayload(
    val name: String,
    val description: String? = null,
    val capacity: Int = 5,
    val xpMultiplier: Float = 1.3f,
    val validFromTime: String? = null,
    val validUntilTime: String? = null
)

@Serializable
data class JoinRoomPayload(
    val invitationCode: String
)

@Serializable
data class RoomResponseDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val capacity: Int,
    val creatorId: String,
    val status: String,
    val xpMultiplier: Float,
    val invitationCode: String? = null,
    val qrCode: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null
)

@Serializable
data class MemberResponseDto(
    val message: String,
    val roomId: String? = null
)