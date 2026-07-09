package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.ui.component.GuildCard
import com.example.focus.ui.component.SectionLabel
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isMusicEnabled by viewModel.isMusicEnabled.collectAsState()

    DungeonBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("CONFIGURACIÓN", color = AncientGold, fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AncientGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DungeonNoir700.copy(alpha = 0.8f))
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                GuildCard(
                    modifier = Modifier.fillMaxWidth(),
                    animatedBorder = true
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SectionLabel(text = "SONIDO Y ATMÓSFERA")
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Música de Fondo",
                                    color = AncientGold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Melodías épicas para el estudio",
                                    color = SteelSilver500,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            
                            Switch(
                                checked = isMusicEnabled,
                                onCheckedChange = { viewModel.toggleMusic(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AncientGold,
                                    checkedTrackColor = AmberFlame.copy(alpha = 0.5f),
                                    uncheckedThumbColor = SteelSilver500,
                                    uncheckedTrackColor = DungeonNoir500
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "v1.2 — Edición Legendaria",
                    style = MaterialTheme.typography.labelSmall,
                    color = SteelSilver.copy(alpha = 0.3f)
                )
            }
        }
    }
}
