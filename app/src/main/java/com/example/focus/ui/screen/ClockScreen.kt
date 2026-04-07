package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focus.viewmodel.ClockViewModel

@Composable
fun ClockScreen(
    clockViewModel: ClockViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by clockViewModel.state.collectAsStateWithLifecycle()

    val txtBtnStartStopwatch: String = "Aventura (modo normal)"
    val txtBtnStartTimer: String = "Aventura a Contrareloj"
    val txtBtnReturn: String = "Regresar"
    val txtLblMinutesInput: String = "Minutos"

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Botón para volver (Visible solo si no está corriendo el reloj)
        if (!state.isRunning) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text("← Volver a Debug")
            }
        }

        Text(
            text = state.formattedTime,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (!state.isRunning) {
            Button(onClick = {clockViewModel.startStopwatch()}) {
                Text(txtBtnStartStopwatch)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.inputMinutes,
                    onValueChange = { clockViewModel.onInputChanged(it) },
                    label = { Text(txtLblMinutesInput) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(100.dp)
                )
                Button(onClick = { clockViewModel.startTimer() }) {
                    Text(txtBtnStartTimer)
                }
            }
        } else {
            Button(onClick = { clockViewModel.stopClock() }) {
                Text(txtBtnReturn)
            }
        }
    }
}