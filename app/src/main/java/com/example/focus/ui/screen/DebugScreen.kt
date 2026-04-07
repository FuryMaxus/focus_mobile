package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    onTimerButtonClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: (String, String) -> Unit,
    onFetchUsersClick: () -> Unit,
    usersList: Map<String, String>
) {
    var email by remember { mutableStateOf("test@example.com") }
    var password by remember { mutableStateOf("12345") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pruebas de Backend", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { onLoginClick(email, password) }) {
                Text("Login")
            }
            Button(onClick = { onRegisterClick(email, password) }) {
                Text("Registrar")
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Button(onClick = onFetchUsersClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
            Text("Ver Usuarios Registrados")
        }
        
        if (usersList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Lista de Usuarios (Base de Datos):", style = MaterialTheme.typography.titleSmall)
                    usersList.forEach { (mail, hash) ->
                        Text("$mail: ${hash.take(15)}...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        
        Button(
            onClick = onTimerButtonClick, 
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Ir al Reloj Focus")
        }
    }
}