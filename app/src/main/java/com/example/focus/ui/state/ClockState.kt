package com.example.focus.ui.state

import android.annotation.SuppressLint

data class ClockState(
    val timeInSeconds: Int = 0,
    val isRunning: Boolean = false,

    val message: String = "",
    val isError: Boolean = false
) {
    val formattedTime: String
        @SuppressLint("DefaultLocale")
        get() {
            val minutes = timeInSeconds / 60
            val seconds = timeInSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}