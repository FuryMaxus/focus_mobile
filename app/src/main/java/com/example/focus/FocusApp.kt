package com.example.focus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FocusApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channelId = "timer_channel"
        val channelName = "Cronómetro de Misión"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Muestra el tiempo restante de la sesión actual"
            
            // Configurar sonido personalizado
            // val soundUri = android.net.Uri.parse("${android.content.ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/raw/notification_sound")
            // val audioAttributes = android.media.AudioAttributes.Builder()
            //     .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
            //     .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
            //     .build()
            // setSound(soundUri, audioAttributes)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}