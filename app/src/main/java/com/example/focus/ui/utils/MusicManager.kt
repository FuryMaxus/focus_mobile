package com.example.focus.ui.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.focus.R
import com.example.focus.data.local.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) {
    private var mediaPlayer: MediaPlayer? = null

    fun startMusic() {
        val isEnabled = runBlocking { userPreferences.isMusicEnabled.first() }
        if (!isEnabled) return

        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(context, R.raw.background_music)
                mediaPlayer?.isLooping = true
                mediaPlayer?.setVolume(0.4f, 0.4f)
                mediaPlayer?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }
}
