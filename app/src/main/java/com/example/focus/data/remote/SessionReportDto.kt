package com.example.focus.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SessionReportItemDto(
    val id: String,
    val userId: String,
    val activityType: String,
    val startTime: String,
    val endTime: String,
    val expEarned: Int,
    val roomId: String? = null
)

@Serializable
data class SessionReportResponseDto(
    val reports: List<SessionReportItemDto>,
    val totalCount: Int
)
