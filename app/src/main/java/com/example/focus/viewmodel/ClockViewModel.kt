package com.example.focus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focus.ui.state.ClockState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClockViewModel: ViewModel() {

    private val _state = MutableStateFlow(ClockState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null

    fun onInputChanged(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            _state.update { it.copy(inputMinutes = newValue) }
        }
    }

    fun startStopwatch(){
        if (_state.value.isRunning) return

        _state.update {
            it.copy(
                isRunning = true,
                isCountdown = false,
                timeInSeconds = 0L
            )
        }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _state.update {
                    it.copy(
                        timeInSeconds = it.timeInSeconds + 1
                    )
                }
            }
        }
    }

    fun startTimer() {
        if (_state.value.isRunning) return

        val minutes = _state.value.inputMinutes.toIntOrNull() ?: 0

        if (minutes <= 0) return

        val totalSeconds = (minutes * 60).toLong()
        _state.update {
            it.copy(
                isRunning = true,
                isCountdown = true,
                timeInSeconds = totalSeconds
            )
        }

        timerJob = viewModelScope.launch {
            while (_state.value.timeInSeconds > 0) {
                delay(1000L)
                _state.update {
                    it.copy(timeInSeconds = it.timeInSeconds - 1)
                }
            }
            stopClock()
            onTimerFinished()

        }
    }

    fun stopClock() {
        timerJob?.cancel()
        timerJob = null
        _state.update {
            it.copy(isRunning = false)
        }
    }

    private fun onTimerFinished() {
       //Here goes what happen after the clock business ends, like the rewards and such
        //we have to work on this when the users stuff are ready
    }
}