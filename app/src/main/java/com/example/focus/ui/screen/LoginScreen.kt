package com.example.focus.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.focus.ui.theme.*
import com.example.focus.ui.component.OrnamentalDivider
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

    val mensaje   by viewModel.mensaje.collectAsState()
    val isError   by viewModel.isError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(InkBlack, DungeonNoir, DungeonNoir700)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Encabezado ──────────────────────────────────────
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

            Spacer(modifier = Modifier.height(12.dp))
            OrnamentalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // ── Card de formulario ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DungeonNoir700)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(SaddleBrown, AncientGold700, SaddleBrown)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {

                    // ── Campo email ─────────────────────────────
                    Text(
                        text = "CORREO DEL AVENTURERO",
                        style = MaterialTheme.typography.labelSmall,
                        color = AncientGold700,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = localEmail,
                        onValueChange = {
                            localEmail = it
                            viewModel.onEmailChange(it)
                        },
                        placeholder = { Text("tu@correo.com", color = SteelSilver500) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AncientGold,
                            unfocusedBorderColor = SteelSilver200,
                            focusedTextColor     = SteelSilver,
                            unfocusedTextColor   = SteelSilver,
                            cursorColor          = AncientGold,
                            focusedContainerColor   = DungeonNoir500,
                            unfocusedContainerColor = DungeonNoir500
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Campo contraseña ────────────────────────
                    Text(
                        text = "PALABRA DE PASO SECRETA",
                        style = MaterialTheme.typography.labelSmall,
                        color = AncientGold700,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = localPassword,
                        onValueChange = {
                            localPassword = it
                            viewModel.onPasswordChange(it)
                        },
                        placeholder = { Text("••••••••", color = SteelSilver500) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AncientGold,
                            unfocusedBorderColor = SteelSilver200,
                            focusedTextColor     = SteelSilver,
                            unfocusedTextColor   = SteelSilver,
                            cursorColor          = AncientGold,
                            focusedContainerColor   = DungeonNoir500,
                            unfocusedContainerColor = DungeonNoir500
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Botón principal ─────────────────────────
                    Button(
                        onClick = {
                            viewModel.login(onSuccess = {
                                onNavigateHome()
                            })
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor  = AmberFlame,
                            contentColor    = DungeonNoir,
                            disabledContainerColor = AmberFlame700
                        ),
                        shape = RoundedCornerShape(4.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color     = DungeonNoir,
                                modifier  = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "ENTRAR AL GREMIO",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // ── Mensaje de feedback ─────────────────────
                    if (mensaje.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isError) DragonRedSurface else DungeonNoir500)
                                .border(
                                    width = 1.dp,
                                    color = if (isError) DragonRed else DungeonGreen,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = mensaje,
                                color = if (isError) AmberFlame200 else AncientGold200,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Link a registro ─────────────────────────────────
            TextButton(
                onClick = { onNavigateToRegister() }
            ) {
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