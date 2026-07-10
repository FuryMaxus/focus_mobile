package com.example.focus.data.remote
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardItemDto(
    val userId: String,
    val totalXp: Int
)

@Serializable
data class GraphItemDto(
    val date: String,
    val xp: Int
)