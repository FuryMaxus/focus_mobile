package com.example.focus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.focus.data.local.entity.FocusSessionEntity

@Dao
@JvmSuppressWildcards
interface FocusSessionDao {

    @Insert
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Query("SELECT * FROM pending_sessions")
    suspend fun getAllPendingSessions(): List<FocusSessionEntity>

    @Query("DELETE FROM pending_sessions WHERE id IN (:sessionIds)")
    suspend fun deleteSessionsByIds(sessionIds: List<Int>): Int

    @Query("DELETE FROM pending_sessions")
    suspend fun clearAll(): Int
}