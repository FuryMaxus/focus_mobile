package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.ui.component.*
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.DMCreateRoomViewModel

@Composable
fun DMCreateRoomScreen(
    viewModel: DMCreateRoomViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val capacity by viewModel.capacity.collectAsState()
    val xpMultiplier by viewModel.xpMultiplier.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val isError by viewModel.isError.collectAsState()

    DungeonBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FORJAR NUEVO GREMIO",
                style = MaterialTheme.typography.headlineMedium,
                color = AncientGold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Como Maestro, define las reglas de tu dominio",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = SteelSilver500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
            GuildDivider(modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(modifier = Modifier.height(32.dp))

            GuildCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = AncientGold,
                animatedBorder = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    SectionLabel(text = "DATOS DEL ARTEFACTO")

                    GuildTextField(
                        value = name,
                        onValueChange = viewModel::onNameChange,
                        label = "NOMBRE DEL GREMIO",
                        placeholder = "Ej: Los Sabios de Invierno",
                        leadingIcon = "🏰"
                    )

                    GuildTextField(
                        value = description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "DESCRIPCIÓN (OPCIONAL)",
                        placeholder = "Una sala para estudiar en silencio...",
                        leadingIcon = "📜"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GuildTextField(
                            value = capacity,
                            onValueChange = viewModel::onCapacityChange,
                            label = "CAPACIDAD",
                            placeholder = "5",
                            leadingIcon = "👥",
                            modifier = Modifier.weight(1f)
                        )
                        GuildTextField(
                            value = xpMultiplier,
                            onValueChange = viewModel::onMultiplierChange,
                            label = "MULTI. XP",
                            placeholder = "1.3",
                            leadingIcon = "✨",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GuildPrimaryButton(
                        text = "FORJAR SALA",
                        onClick = { viewModel.createRoom(onSuccess = onNavigateBack) },
                        loading = isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        animatedBorder = true
                    )

                    if (mensaje.isNotEmpty()) {
                        GuildFeedback(
                            message = mensaje,
                            isError = isError,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GuildOutlineButton(
                text = "CANCELAR RITUAL",
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
