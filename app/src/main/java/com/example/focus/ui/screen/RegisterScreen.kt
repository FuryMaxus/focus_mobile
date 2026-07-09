package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.focus.viewmodel.RegisterViewModel
import com.example.focus.ui.theme.*
import com.example.focus.ui.component.GuildCard
import com.example.focus.ui.component.GuildDivider
import com.example.focus.ui.component.GuildFeedback
import com.example.focus.ui.component.GuildPrimaryButton
import com.example.focus.ui.component.GuildTextField
import com.example.focus.ui.component.RarityBadge
import com.example.focus.ui.component.SectionLabel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {

    var localEmail by remember { mutableStateOf("") }
    var localPassword by remember { mutableStateOf("") }

    val role      by viewModel.role.collectAsState()
    val mensaje   by viewModel.mensaje.collectAsState()
    val isError   by viewModel.isError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    DungeonBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "FORJAR NUEVA CUENTA",
                style = MaterialTheme.typography.headlineMedium,
                color = AncientGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Inscribe tu nombre en los registros del gremio",
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
                    RarityBadge(text = "✦ NUEVO AVENTURERO", accent = AncientGold)

                    Spacer(modifier = Modifier.height(20.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "⚔  Mínimo 8 caracteres para fortaleza de guerrero",
                        style = MaterialTheme.typography.labelSmall,
                        color = SteelSilver500,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    SectionLabel(text = "ELEGIR ROL EN EL GREMIO")
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RoleOption(
                            label = "DM (MAESTRO)",
                            isSelected = role == "dm",
                            onClick = { viewModel.onRoleChange("dm") },
                            modifier = Modifier.weight(1f)
                        )
                        RoleOption(
                            label = "JUGADOR",
                            isSelected = role == "student",
                            onClick = { viewModel.onRoleChange("student") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    GuildPrimaryButton(
                        text = "INSCRIBIRSE EN EL GREMIO",
                        leading = "✦",
                        onClick = { viewModel.register(onSuccess = { onNavigateToLogin() }) },
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

            TextButton(onClick = { onNavigateToLogin() }) {
                Text(
                    text = "¿Ya eres miembro? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = SteelSilver500
                )
                Text(
                    text = "Entra al gremio",
                    style = MaterialTheme.typography.bodySmall,
                    color = AncientGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RoleOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) AmberFlame.copy(alpha = 0.1f) else DungeonNoir500)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = if (isSelected) GoldEdgeBrush else GuildBorderBrush,
                shape = RoundedCornerShape(6.dp)
            )
            .then(if (isSelected) Modifier.guildGlow(color = AncientGold, radius = 8.dp, shape = RoundedCornerShape(6.dp), alpha = 0.3f) else Modifier)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // El "puntito" o indicador de selección (Cassette button style)
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AncientGold else Color.Transparent)
                    .border(1.dp, if (isSelected) AncientGold else SteelSilver500, CircleShape)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) AncientGold else SteelSilver500,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
