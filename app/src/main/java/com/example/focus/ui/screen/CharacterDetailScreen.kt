package com.example.focus.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focus.ui.component.*
import com.example.focus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterName: String,
    hatName: String,
    onNavigateBack: () -> Unit
) {
    val character = GuildCharacter.fromName(characterName)
    val baseHat = GuildHat.fromName(hatName)
    
    // ESTADO PARA EL AJUSTE MANUAL (Iniciamos con el offset base del personaje)
    var manualOffsetX by remember { mutableStateOf(character.hatOffsetX * 300f) }
    var manualOffsetY by remember { mutableStateOf(character.hatOffsetY * 300f) }
    var isAdjusting by remember { mutableStateOf(false) }

    DungeonBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(character.displayName.uppercase(), color = AncientGold, fontWeight = FontWeight.Black) },
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .guildGlow(color = AncientGold, radius = 50.dp, alpha = 0.4f),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedCharacter(
                        character = character,
                        size = 300.dp,
                        pose = CharacterPose.Idle,
                        hat = baseHat,
                        customHatOffset = Offset(manualOffsetX, manualOffsetY)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- PANEL DE SASTRERÍA (SLIDERS) ---
                if (!isAdjusting) {
                    GuildOutlineButton(
                        text = "AJUSTAR GORRO",
                        onClick = { isAdjusting = true },
                        modifier = Modifier.height(40.dp)
                    )
                } else {
                    GuildCard(
                        modifier = Modifier.fillMaxWidth(),
                        animatedBorder = true,
                        glowColor = AmberFlame
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("DESPLAZAMIENTO X: ${manualOffsetX.toInt()}", color = AncientGold, style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = manualOffsetX,
                                onValueChange = { manualOffsetX = it },
                                valueRange = -150f..150f,
                                colors = SliderDefaults.colors(thumbColor = AncientGold, activeTrackColor = AncientGold)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("DESPLAZAMIENTO Y: ${manualOffsetY.toInt()}", color = AncientGold, style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = manualOffsetY,
                                onValueChange = { manualOffsetY = it },
                                valueRange = -300f..100f,
                                colors = SliderDefaults.colors(thumbColor = AncientGold, activeTrackColor = AncientGold)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            GuildPrimaryButton(
                                text = "LISTO",
                                onClick = { isAdjusting = false },
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            )
                        }
                    }
                }
                // ------------------------------------

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "VÍNCULO LEGENDARIO",
                    style = MaterialTheme.typography.labelLarge,
                    color = AncientGold700,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Este aventurero es tu reflejo en la mazmorra. Cada minuto de estudio fortalece vuestra conexión espiritual.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SteelSilver,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
