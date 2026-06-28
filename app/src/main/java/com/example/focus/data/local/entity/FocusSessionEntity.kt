package com.example.focus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityType: String,
    val startTime: String,
    val endTime: String,
    val roomId: String?,
    val xpMultiplier: Float
)
