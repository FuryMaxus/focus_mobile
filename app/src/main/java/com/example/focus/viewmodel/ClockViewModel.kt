package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.data.local.UserPreferences
import com.example.focus.repository.FocusSessionRepository
import com.example.focus.ui.component.GuildHat
import com.example.focus.ui.state.ClockState
import com.example.focus.ui.state.TimerMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val selectedCharacter: StateFlow<String> = userPreferences.getCharacter
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Duende"
        )

    val equippedHat: StateFlow<GuildHat> = userPreferences.getEquippedHat
        .map { GuildHat.fromName(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GuildHat.Ninguno
        )

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

    fun setMode(mode: TimerMode) {
        if(!_state.value.isRunning) {
            _state.update { it.copy(mode = mode, timeInSeconds = 0) }
        }
    }

    fun setTargetTime(minutes: Int) {
        if (!_state.value.isRunning) {
            _state.update { it.copy(targetTimeInSecond = minutes*60) }
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
                    val currentState = _state.value
                    if(currentState.mode == TimerMode.TIME_TRIAL && elapsed >= currentState.targetTimeInSecond){
                        _state.update { it.copy(timeInSeconds = currentState.targetTimeInSecond) }
                        finishAndSave()
                    } else {
                        _state.update { it.copy(timeInSeconds = elapsed.toInt()) }
                    }

                }
            }
        }
    }


    fun finishAndSave() {
        if (!_state.value.isRunning) return
        val finalState = _state.value
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

        val isFailedTimeTrial = finalState.mode == TimerMode.TIME_TRIAL && currentSeconds < finalState.targetTimeInSecond
        val activityType = if(finalState.mode == TimerMode.TIME_TRIAL) "TIME_TRIAL" else "NORMAL"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                focusSessionRepository.saveSessionAndSync(
                    activityType = activityType,
                    startTime = currentStartTime,
                    endTime = endTime
                )

                _state.update {
                    it.copy(
                        timeInSeconds = 0,
                        message = if (isFailedTimeTrial) "Mision fallida: Te rendiste antes de tiempo."
                                else "¡Mision completada!",
                        isError = isFailedTimeTrial
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
