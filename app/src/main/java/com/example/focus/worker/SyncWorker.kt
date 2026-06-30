package com.example.focus.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.focus.data.local.dao.FocusSessionDao
import com.example.focus.data.remote.SessionItem
import com.example.focus.data.remote.SyncPayload
import com.example.focus.repository.UserStatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val focusSessionDao: FocusSessionDao,
    private val userStatsRepository: UserStatsRepository
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        try {
            val pendingEntities = focusSessionDao.getAllPendingSessions()

            if (pendingEntities.isEmpty()) {
                return@withContext Result.success()
            }

            val sessionItems = pendingEntities.map { entity ->
                SessionItem(
                    activityType = entity.activityType,
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    roomId = entity.roomId,
                    xpMultiplier = entity.xpMultiplier
                )
            }

            val payload = SyncPayload(sessions = sessionItems)

            val response = userStatsRepository.syncBatchSessions(payload)

            return@withContext response.fold(
                onSuccess = {
                    val idsToDelete = pendingEntities.map { it.id }
                    focusSessionDao.deleteSessionsByIds(idsToDelete)
                    Result.success()
                },
                onFailure = { error ->
                    if (error is IOException) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            )
        } catch (e: Exception) {
            return@withContext Result.failure()
        }
    }
}