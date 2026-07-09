package com.example.focus.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.focus.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class TimerService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var baseTimeMillis: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        baseTimeMillis = intent?.getLongExtra("BASE_TIME_MILLIS", System.currentTimeMillis())
            ?: System.currentTimeMillis()

        val notification = createNotification("00:00")

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(1, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        startClockTicks()

        return START_NOT_STICKY
    }

    private fun startClockTicks() {
        serviceScope.launch {
            while (isActive) {
                val elapsedSeconds = (System.currentTimeMillis() - baseTimeMillis) / 1000
                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                try {
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, createNotification(formattedTime))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(1000L.milliseconds)
            }
        }
    }

    private fun createNotification(timeStr: String): Notification {
        val rootIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, rootIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // val soundUri = android.net.Uri.parse("${android.content.ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/raw/notification_sound")

        return NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle("Misión en Curso ⚔")
            .setContentText("Tiempo concentrado: $timeStr")
            .setSmallIcon(android.R.drawable.ic_media_play) //aqui se cambia si se quiere personalizar el rectangulo
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            // .setSound(soundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }
}