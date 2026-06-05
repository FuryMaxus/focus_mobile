package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.focus.network.RetrofitClient
import com.example.focus.network.SessionItem
import com.example.focus.network.SyncPayload
import com.example.focus.network.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockScreen(navController: NavController) {
    val context = LocalContext.current // Necesario para guardar las estadísticas
    val scope = rememberCoroutineScope()
    var isRunning by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }

    var startTime by remember { mutableStateOf<ZonedDateTime?>(null) }
    var mensajeResultado by remember { mutableStateOf("") }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            seconds++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temporizador Focus") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val minutesStr = (seconds / 60).toString().padStart(2, '0')
            val secondsStr = (seconds % 60).toString().padStart(2, '0')

            Text(text = "$minutesStr:$secondsStr", style = MaterialTheme.typography.displayLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (!isRunning) {
                    Button(onClick = {
                        isRunning = true
                        startTime = ZonedDateTime.now(ZoneOffset.UTC)
                        mensajeResultado = ""
                    }) {
                        Text(if (seconds == 0) "Iniciar Estudio" else "Reanudar")
                    }
                } else {
                    Button(onClick = { isRunning = false }) { Text("Pausar") }

                    Button(
                        onClick = {
                            isRunning = false
                            val endTime = ZonedDateTime.now(ZoneOffset.UTC)
                            val formatter = DateTimeFormatter.ISO_INSTANT

                            if (startTime != null && seconds > 0) {
                                scope.launch {
                                    try {
                                        mensajeResultado = "Guardando sesión..."
                                        val sessionItem = SessionItem(
                                            // ¡AQUÍ ESTÁ LA CORRECCIÓN CLAVE!
                                            activity_type = "FOCUS",
                                            start_time = formatter.format(startTime),
                                            end_time = formatter.format(endTime)
                                        )
                                        val response = RetrofitClient.apiService.syncSessions(SyncPayload(listOf(sessionItem)))

                                        if (response.isSuccessful) {
                                            val stats = response.body()
                                            if (stats != null) {
                                                // ¡Mágia! Guardamos los puntos en la base local
                                                TokenManager(context).saveStats(stats.current_level, stats.total_exp_gained)
                                            }
                                            mensajeResultado = "¡Sesión guardada! Nivel: ${stats?.current_level} (+${stats?.total_exp_gained} EXP)"
                                            seconds = 0
                                            startTime = null
                                        } else {
                                            mensajeResultado = "Error del servidor: ${response.code()}"
                                        }
                                    } catch (e: Exception) {
                                        mensajeResultado = "Error de red: ${e.message}"
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Terminar y Guardar") }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = mensajeResultado, color = MaterialTheme.colorScheme.primary)
        }
    }
}