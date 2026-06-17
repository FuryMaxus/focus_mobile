package com.example.focus.repository

import com.example.focus.data.local.UserPreferences
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
    suspend fun syncSession(session: SessionItem): Result<SyncResponse> {
        return try {
            val payload = SyncPayload(sessions = listOf(session))

            val response = apiService.syncSessions(payload)

            if (response.isSuccessful) {
                val syncData = response.body()
                if (syncData != null) {
                    userPreferences.saveLevelAndExp(
                        level = syncData.currentLevel,
                        exp = syncData.totalExpGained
                    )
                    Result.success(syncData)
                } else {
                    Result.failure(Exception("El servidor respondió sin datos (Body nulo)"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}