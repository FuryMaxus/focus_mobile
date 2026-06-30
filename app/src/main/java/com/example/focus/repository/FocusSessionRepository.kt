package com.example.focus.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.focus.data.local.UserPreferences
import com.example.focus.data.local.dao.FocusSessionDao
import com.example.focus.data.local.entity.FocusSessionEntity
import com.example.focus.worker.SyncWorker
import kotlinx.coroutines.flow.firstOrNull
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusSessionRepository @Inject constructor(
    private val focusSessionDao: FocusSessionDao,
    private val workManager: WorkManager,
    private val userPreferences: UserPreferences
) {
    suspend fun saveSessionAndSync(
        activityType: String,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime
    ) {
        val formatter = DateTimeFormatter.ISO_INSTANT

        val currentRoomId = userPreferences.getEquippedRoomId.firstOrNull()
        val currentMultiplier = userPreferences.getEquippedRoomMultiplier.firstOrNull() ?: 1.0f

        val newSession = FocusSessionEntity(
            activityType = activityType,
            startTime = formatter.format(startTime),
            endTime = formatter.format(endTime),
            roomId = currentRoomId,
            xpMultiplier = currentMultiplier
        )

        focusSessionDao.insertSession(newSession)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "SyncSessionsWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}