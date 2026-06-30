package com.example.focus.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.focus.ui.component.GuildCard
import com.example.focus.ui.component.GuildCharacter
import com.example.focus.ui.component.IdleCharacter
import com.example.focus.ui.component.RarityBadge
import com.example.focus.ui.component.SectionLabel
import com.example.focus.ui.theme.*
import com.example.focus.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val selectedName by viewModel.selectedCharacter.collectAsState()
    val selected = GuildCharacter.fromName(selectedName)

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

                Spacer(modifier = Modifier.height(16.dp))

                // ── Personaje equipado (preview grande, idle) ────
                GuildCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = AncientGold700,
                    animatedBorder = true
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SectionLabel("PERSONAJE EQUIPADO")

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IdleCharacter(character = selected, size = 150.dp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = selected.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = AncientGold
                        )
                        Text(
                            text = "Tu fiel compañero de aventuras",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = SteelSilver500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Selección de personaje ───────────────────────
                SectionLabel(
                    text = "ELIGE TU PERSONAJE",
                    modifier = Modifier.align(Alignment.Start)
                )

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
                                    selected = character == selected,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.selectCharacter(character.name) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Objetos (placeholder) ────────────────────────
                SectionLabel(
                    text = "OBJETOS DEL MORRAL",
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(4) {
                        EmptyItemSlot(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
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
            .then(
                if (selected)
                    Modifier.guildGlow(color = AmberFlame, radius = 14.dp, shape = shape, alpha = 0.5f)
                else Modifier
            )
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
            .padding(vertical = 14.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                IdleCharacter(character = character, size = 78.dp)
            }

            Text(
                text = character.displayName,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) AncientGold else SteelSilver,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            if (selected) {
                RarityBadge(text = "EQUIPADO", accent = AmberFlame)
            } else {
                Text(
                    text = "Tocar para equipar",
                    style = MaterialTheme.typography.labelSmall,
                    color = SteelSilver500
                )
            }
        }
    }
}

// ── Slot de objeto vacío (placeholder) ──────────────────────────
@Composable
private fun EmptyItemSlot(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .background(DungeonNoir500)
                .border(1.dp, SteelSilver200, shape),
            contentAlignment = Alignment.Center
        ) {
            Text("✦", color = SteelSilver200, fontSize = 18.sp)
        }
        Text(
            text = "Próximamente",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = SteelSilver500,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
