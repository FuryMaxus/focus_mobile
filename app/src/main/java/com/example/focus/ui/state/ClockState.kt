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
            val days = timeInSeconds / 86400
            val hours = (timeInSeconds % 86400) / 3600
            val minutes = (timeInSeconds % 3600) / 60
            val seconds = timeInSeconds % 60

            return when {
                days > 0 -> String.format("%dd %02dh %02dm", days, hours, minutes)
                hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
                else -> String.format("%02d:%02d", minutes, seconds)
            }
        }
}