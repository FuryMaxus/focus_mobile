package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.local.UserPreferences
import com.example.focus.repository.FocusSessionRepository
import com.example.focus.ui.state.ClockState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import java.time.Duration


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

        startTime = ZonedDateTime.now(ZoneOffset.UTC)

        _state.update { it.copy(isRunning = true, message = "") }

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L.milliseconds)
                startTime?.let { start ->
                    val now = ZonedDateTime.now(ZoneOffset.UTC)
                    val elapsed = Duration.between(start, now).seconds
                    _state.update { it.copy(timeInSeconds = elapsed.toInt()) }
                }
            }
        }
    }


    fun finishAndSave() {
        if (!_state.value.isRunning) return

        _state.update { it.copy(isRunning = false, message = "Guardando sesión...", isError = false) }


        timerJob?.cancel()
        timerJob = null

        val currentSeconds = _state.value.timeInSeconds
        val currentStartTime = startTime

        if (currentSeconds == 0 || currentStartTime == null) {
            _state.update { it.copy(timeInSeconds = 0) }
            return
        }

        val endTime = ZonedDateTime.now(ZoneOffset.UTC)


        viewModelScope.launch(Dispatchers.IO) {
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
