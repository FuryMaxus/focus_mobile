package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.remote.SessionItem
import com.example.focus.repository.UserStatsRepository
import com.example.focus.ui.state.ClockState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val userStatsRepository: UserStatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClockState())
    val state: StateFlow<ClockState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: ZonedDateTime? = null


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
        val formatter = DateTimeFormatter.ISO_INSTANT

        _state.update { it.copy(message = "Guardando sesión...", isError = false) }

        viewModelScope.launch {
            val sessionItem = SessionItem(
                activityType = "FOCUS",
                startTime = formatter.format(currentStartTime),
                endTime = formatter.format(endTime),
                roomId = null
            )

            userStatsRepository.syncSession(sessionItem).fold(
                onSuccess = { stats ->
                    _state.update {
                        it.copy(
                            timeInSeconds = 0,
                            message = "¡Sesión guardada! Nivel: ${stats.currentLevel} (+${stats.totalExpGained} EXP)",
                            isError = false
                        )
                    }
                    startTime = null
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            message = "Error: ${error.message}",
                            isError = true
                        )
                    }
                }
            )
        }
    }
}
