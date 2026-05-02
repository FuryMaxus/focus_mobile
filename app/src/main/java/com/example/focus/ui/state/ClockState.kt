package com.example.focus.ui.state

import android.annotation.SuppressLint

data class ClockState(
    val timeInSeconds: Long = 0L,
    val isRunning: Boolean = false,
    val isCountdown: Boolean = false,
    val inputMinutes: String = "25"
) {
    val formattedTime: String
        @SuppressLint("DefaultLocale")
        get() {
            val minutes = timeInSeconds / 60
            val seconds = timeInSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}
