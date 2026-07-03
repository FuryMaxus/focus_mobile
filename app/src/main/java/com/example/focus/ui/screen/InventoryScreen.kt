package com.example.focus.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.ui.component.*
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String, String) -> Unit
) {
    val selectedName by viewModel.selectedCharacter.collectAsState()
    val selectedCharacter = GuildCharacter.fromName(selectedName)
    
    val equippedHat by viewModel.equippedHat.collectAsState()
    val ownedHats by viewModel.ownedHats.collectAsState()

    Scaffold(
        containerColor = DungeonNoir,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "INVENTARIO",
                            style = MaterialTheme.typography.titleLarge,
                            color = AncientGold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "BÓVEDA DEL AVENTURERO",
                            style = MaterialTheme.typography.labelSmall,
                            color = SteelSilver500,
                            letterSpacing = 2.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = AncientGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DungeonNoir700)
            )
        }
    ) { paddingValues ->

        DungeonBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                // ── PERSONAJE XL (Preview) ──────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Resplandor de pedestal
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .offset(y = 70.dp)
                            .guildGlow(color = AncientGold, radius = 30.dp, alpha = 0.3f)
                            .background(
                                Brush.radialGradient(listOf(AncientGold.copy(alpha = 0.2f), Color.Transparent)),
                                shape = CircleShape
                            )
                    )
                    
                    IdleCharacter(
                        character = selectedCharacter,
                        size = 220.dp,
                        hat = equippedHat,
                        modifier = Modifier.clickable { onNavigateToDetail(selectedCharacter.name, equippedHat.name) }
                    )
                }

                Text(
                    text = selectedCharacter.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = AncientGold,
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                GuildDivider(modifier = Modifier.fillMaxWidth(0.6f))
                Spacer(modifier = Modifier.height(24.dp))

                // ── Morral (Objetos / Hats) ──────────────────────
                SectionLabel(text = "OBJETOS DEL MORRAL")
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Slot para quitar gorro
                    ItemSlot(
                        hat = GuildHat.Ninguno,
                        isSelected = equippedHat == GuildHat.Ninguno,
                        onClick = { viewModel.equipHat(GuildHat.Ninguno) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Hats que posee el usuario
                    ownedHats.forEach { hat ->
                        ItemSlot(
                            hat = hat,
                            isSelected = equippedHat == hat,
                            onClick = { viewModel.equipHat(hat) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Rellenar con vacíos si hay pocos items
                    repeat(3 - ownedHats.size) {
                        EmptyItemSlot(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Selección de personaje ───────────────────────
                SectionLabel(text = "ELIGE TU AVENTURERO")
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GuildCharacter.entries.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { character ->
                                CharacterSlot(
                                    character = character,
                                    selected = character == selectedCharacter,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.selectCharacter(character.name) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// ── Slot de Objeto (Gorro) ──────────────────────────────────────
@Composable
private fun ItemSlot(
    hat: GuildHat,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .background(if (isSelected) AmberFlame.copy(alpha = 0.15f) else DungeonNoir500)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    brush = if (isSelected) GoldEdgeBrush else androidx.compose.ui.graphics.SolidColor(SaddleBrown),
                    shape = shape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (hat == GuildHat.Ninguno) {
                Text("✖", color = SteelSilver500, fontSize = 20.sp)
            } else {
                Image(
                    painter = painterResource(id = hat.resId),
                    contentDescription = hat.displayName,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
        Text(
            text = hat.displayName.split(" ").last(), // Nombre corto
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) AncientGold else SteelSilver500,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// ── Slot seleccionable de personaje ─────────────────────────────
@Composable
private fun CharacterSlot(
    character: GuildCharacter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(SurfaceSheenBrush)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                brush = if (selected) GoldEdgeBrush
                else androidx.compose.ui.graphics.SolidColor(SteelSilver200),
                shape = shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IdleCharacter(character = character, size = 80.dp)

            Text(
                text = character.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) AncientGold else SteelSilver,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            if (selected) {
                RarityBadge(text = "EQUIPADO", accent = AmberFlame)
            }
        }
    }
}

// ── Slot de objeto vacío ────────────────────────────────────────
@Composable
private fun EmptyItemSlot(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .background(DungeonNoir500)
            .border(1.dp, SaddleBrown.copy(alpha = 0.3f), shape),
        contentAlignment = Alignment.Center
    ) {
        Text("✦", color = SaddleBrown.copy(alpha = 0.5f), fontSize = 18.sp)
    }
}
