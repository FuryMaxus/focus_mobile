package com.example.focus.data.remote

data class SessionItem(
    val activityType: String,
    val startTime: String,
    val endTime: String,
    val roomId: String? = null,
    val xpMultiplier: Float = 1.0f
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
    val processedSessionsCount: Int,
    val totalExpGained: Int,
    val currentLevel: Int,
    val leveledUp: Boolean,
    val levelsGained: Int,
    val rewards: List<RewardItem>
)