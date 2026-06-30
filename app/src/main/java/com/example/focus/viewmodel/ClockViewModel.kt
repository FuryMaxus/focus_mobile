package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.focus.data.local.UserPreferences
import com.example.focus.data.local.dao.FocusSessionDao
import com.example.focus.data.local.entity.FocusSessionEntity
import com.example.focus.data.remote.SessionItem
import com.example.focus.repository.FocusSessionRepository
import com.example.focus.repository.UserStatsRepository
import com.example.focus.ui.state.ClockState
import com.example.focus.worker.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(ClockState())
    val state: StateFlow<ClockState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: ZonedDateTime? = null

    init {
        viewModelScope.launch {
            val multiplier = userPreferences.getEquippedRoomMultiplier.firstOrNull() ?: 1.0f
            if (multiplier > 1.0f) {
                _state.update { it.copy(message = "Gremio Activo: Bonus de ${multiplier}x XP") }
            }
        }
    }

    fun startStopwatch() {
        if (_state.value.isRunning) return

        if (startTime == null) {
            startTime = ZonedDateTime.now(ZoneOffset.UTC)
        }

        _state.update { it.copy(isRunning = true, message = "") }

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L.milliseconds)
                _state.update { it.copy(timeInSeconds = it.timeInSeconds + 1) }
            }
        }
    }

    fun pauseClock() {
        timerJob?.cancel()
        _state.update { it.copy(isRunning = false) }
    }


    fun finishAndSave() {
        val currentSeconds = _state.value.timeInSeconds
        val currentStartTime = startTime

        if (currentSeconds == 0 || currentStartTime == null) return

        pauseClock()
        val endTime = ZonedDateTime.now(ZoneOffset.UTC)

        _state.update { it.copy(message = "Guardando sesión...", isError = false) }

        viewModelScope.launch {
            try {
                focusSessionRepository.saveSessionAndSync(
                    activityType = "NORMAL",
                    startTime = currentStartTime,
                    endTime = endTime
                )

                _state.update {
                    it.copy(
                        timeInSeconds = 0,
                        message = "¡Misión completada! Se sincronizará automáticamente.",
                        isError = false
                    )
                }
                startTime = null

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        message = "Error al guardar en el pergamino local: ${e.message}",
                        isError = true
                    )
                }
            }
        }
    }

}
