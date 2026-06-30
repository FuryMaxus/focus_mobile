package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.focus.ui.theme.*
import com.example.focus.ui.component.*
import com.example.focus.viewmodel.LoginViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var localEmail by remember { mutableStateOf("") }
    var localPassword by remember { mutableStateOf("") }

    val mensaje by viewModel.mensaje.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    DungeonBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollSafe()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "INICIAR SESIÓN",
                style = MaterialTheme.typography.headlineMedium,
                color = AncientGold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Las puertas del gremio te esperan",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = SteelSilver500,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            GuildDivider(modifier = Modifier.fillMaxWidth(0.7f))
            Spacer(modifier = Modifier.height(28.dp))
            GuildCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = AncientGold700,
                animatedBorder = true,
                contentPadding = PaddingValues(24.dp)
            ) {
                Column {
                    GuildTextField(
                        value = localEmail,
                        onValueChange = {
                            localEmail = it
                            viewModel.onEmailChange(it)
                        },
                        label = "CORREO DEL AVENTURERO",
                        placeholder = "tu@correo.com",
                        leadingIcon = "✉"
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    GuildTextField(
                        value = localPassword,
                        onValueChange = {
                            localPassword = it
                            viewModel.onPasswordChange(it)
                        },
                        label = "PALABRA DE PASO SECRETA",
                        placeholder = "••••••••",
                        isPassword = true,
                        leadingIcon = "🗝"
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    GuildPrimaryButton(
                        text = "ENTRAR AL GREMIO",
                        leading = "⚔",
                        onClick = { viewModel.login(onSuccess = { onNavigateHome() }) },
                        loading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (mensaje.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        GuildFeedback(
                            message = mensaje,
                            isError = isError,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = { onNavigateToRegister() }) {
                Text(
                    text = "¿Aún no eres miembro? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = SteelSilver500
                )
                Text(
                    text = "Únete al gremio",
                    style = MaterialTheme.typography.bodySmall,
                    color = AncientGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun Modifier.verticalScrollSafe(): Modifier =
    this.verticalScroll(androidx.compose.foundation.rememberScrollState())
