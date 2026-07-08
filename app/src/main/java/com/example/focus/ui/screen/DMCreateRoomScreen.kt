package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.ui.component.*
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.DMCreateRoomViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

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
    val isTimeRestricted by viewModel.isTimeRestricted.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()

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

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Switch(
                            checked = isTimeRestricted,
                            onCheckedChange = viewModel::onTimeRestrictedChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AncientGold,
                                checkedTrackColor = Color(0xFF6B4E0F),
                                uncheckedThumbColor = SteelSilver500,
                                uncheckedTrackColor = Color(0xFF2D2D2D)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Restringir horario de validez",
                            color = if (isTimeRestricted) AncientGold else SteelSilver500,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    AnimatedVisibility(visible = isTimeRestricted) {
                        val context = LocalContext.current
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TimeSelector(
                                label = "HORA INICIO",
                                time = startTime,
                                onClick = {
                                    showTimePicker(context, startTime) { viewModel.onStartTimeChange(it) }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            TimeSelector(
                                label = "HORA FIN",
                                time = endTime,
                                onClick = {
                                    showTimePicker(context, endTime) { viewModel.onEndTimeChange(it) }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
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

@Composable
fun TimeSelector(
    label: String,
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = SteelSilver500,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E1E24))
                .border(1.dp, SteelSilver200.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = time,
                    color = AncientGold,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun showTimePicker(
    context: android.content.Context,
    initialTime: String,
    onTimeSelected: (String) -> Unit
) {
    val parts = initialTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    android.app.TimePickerDialog(
        context,
        android.R.style.Theme_DeviceDefault_Dialog_Alert,
        { _, selectedHour, selectedMinute ->
            val time = String.format(java.util.Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(time)
        },
        hour,
        minute,
        true
    ).show()
}
