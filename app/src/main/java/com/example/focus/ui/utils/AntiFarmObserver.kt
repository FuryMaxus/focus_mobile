package com.example.focus.ui.utils

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.focus.service.TimerService
import android.os.Build
import android.Manifest
import android.app.KeyguardManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AntiFarmObserver(
    isRunning: Boolean,
    timeInSeconds: Int,
    onCheatDetected: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    var cheatCheckJob by remember { mutableStateOf<Job?>(null) }
    val currentTime = rememberUpdatedState(timeInSeconds)
    val currentOnCheatState = rememberUpdatedState(onCheatDetected)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
        }
    )


    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    DisposableEffect(lifecycleOwner, isRunning) {
        val observer = LifecycleEventObserver { _, event ->
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

            if (isRunning) {
                if (event == Lifecycle.Event.ON_STOP) {


                    val serviceIntent = Intent(context, TimerService::class.java).apply {
                        val elapsedMillis = currentTime.value * 1000L
                        val estimatedStartMs = System.currentTimeMillis() - elapsedMillis
                        putExtra("BASE_TIME_MILLIS", estimatedStartMs)
                    }
                    try {
                        context.startForegroundService(serviceIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    cheatCheckJob = coroutineScope.launch {
                        delay(1500L.milliseconds)
                        val isScreenOn = powerManager.isInteractive
                        val isLocked = keyguardManager.isKeyguardLocked

                        if(isScreenOn && !isLocked) {
                            currentOnCheatState.value()
                            context.stopService(Intent(context, TimerService::class.java))
                        }
                    }
                } else if (event == Lifecycle.Event.ON_RESUME) {
                    cheatCheckJob?.cancel()
                    context.stopService(Intent(context, TimerService::class.java))
                }
            }

        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            cheatCheckJob?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.stopService(Intent(context, TimerService::class.java))
        }
    }
}