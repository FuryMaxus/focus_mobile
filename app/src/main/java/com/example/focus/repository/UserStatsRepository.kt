package com.example.focus.repository

import com.example.focus.data.local.UserPreferences
import com.example.focus.data.remote.GraphItemDto
import com.example.focus.data.remote.LeaderboardItemDto
import com.example.focus.data.remote.SessionItem
import com.example.focus.data.remote.SyncPayload
import com.example.focus.data.remote.SyncResponse
import com.example.focus.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStatsRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun syncBatchSessions(payload: SyncPayload): Result<SyncResponse> {
        return try {

            val syncData = apiService.syncSessions(payload)

            userPreferences.saveLevelAndExp(
                level = syncData.currentLevel,
                exp = syncData.totalExp
            )
            Result.success(syncData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
