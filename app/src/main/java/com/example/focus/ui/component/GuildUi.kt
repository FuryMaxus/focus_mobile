package com.example.focus.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focus.ui.theme.AmberFlame
import com.example.focus.ui.theme.AmberFlame200
import com.example.focus.ui.theme.AmberFlame700
import com.example.focus.ui.theme.AncientGold
import com.example.focus.ui.theme.AncientGold200
import com.example.focus.ui.theme.AncientGold700
import com.example.focus.ui.theme.DragonRed
import com.example.focus.ui.theme.DragonRedSurface
import com.example.focus.ui.theme.DungeonGreen
import com.example.focus.ui.theme.DungeonNoir
import com.example.focus.ui.theme.DungeonNoir500
import com.example.focus.ui.theme.DungeonNoir700
import com.example.focus.ui.theme.GuildBorderBrush
import com.example.focus.ui.theme.InkBlack
import com.example.focus.ui.theme.SaddleBrown
import com.example.focus.ui.theme.SaddleBrown700
import com.example.focus.ui.theme.SteelSilver
import com.example.focus.ui.theme.SteelSilver500
import com.example.focus.ui.theme.SurfaceSheenBrush
import com.example.focus.ui.theme.animatedGoldBorder
import com.example.focus.ui.theme.guildGlow

// ═══════════════════════════════════════════════════════════════
//  FOCUS MOBILE — Design System · Componentes de Gremio
//  Bloques reutilizables con acabado premium. Toda pantalla nueva
//  debería componerse con estos en lugar de Box/Button crudos.
// ═══════════════════════════════════════════════════════════════

/**
 * Tarjeta de artefacto: superficie elevada con relleno en gradiente
 * (volumen), borde metálico (estático o con shimmer animado) y un
 * resplandor de color opcional que la separa del fondo.
 */
@Composable
fun GuildCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    glow: Boolean = true,
    glowColor: Color = AncientGold700,
    animatedBorder: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit
) {
    val base = modifier
        .then(if (glow) Modifier.guildGlow(color = glowColor, shape = shape, alpha = 0.40f) else Modifier)
        .clip(shape)
        .background(SurfaceSheenBrush)

    val bordered = if (animatedBorder) {
        base.animatedGoldBorder(shape = shape)
    } else {
        base.border(width = 1.dp, brush = GuildBorderBrush, shape = shape)
    }

    Box(modifier = bordered.padding(contentPadding)) {
        content()
    }
}

/**
 * Botón primario de acción (llama de ámbar) con resplandor y texto
 * grabado. Soporta estado de carga e icono inicial opcional.
 */
@Composable
fun GuildPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leading: String? = null,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .height(54.dp)
            .then(if (enabled && !loading) Modifier.guildGlow(color = AmberFlame, radius = 16.dp, shape = shape, alpha = 0.5f) else Modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = AmberFlame,
            contentColor = InkBlack,
            disabledContainerColor = AmberFlame700,
            disabledContentColor = InkBlack.copy(alpha = 0.6f)
        ),
        shape = shape,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = InkBlack,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
        } else {
            if (leading != null) {
                Text(text = leading, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Botón secundario delineado en oro, para acciones alternativas.
 */
@Composable
fun GuildOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: String? = null,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(54.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = DungeonNoir700,
            contentColor = AncientGold
        ),
        border = BorderStroke(1.dp, GuildBorderBrush),
        shape = shape
    ) {
        if (leading != null) {
            Text(text = leading, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = AncientGold
        )
    }
}

/**
 * Etiqueta de sección grabada, flanqueada por rombos dorados.
 * Para encabezar bloques ("PERGAMINO DE ESTADÍSTICAS", etc.).
 */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AncientGold
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("✦", color = color.copy(alpha = 0.7f), fontSize = 10.sp)
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Insignia de rareza/estado, estilo placa de cuero con borde.
 */
@Composable
fun RarityBadge(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = AmberFlame,
    fill: Color = SaddleBrown700
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(fill)
            .border(1.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Campo de texto temático del gremio: label grabado encima + input
 * con borde dorado al enfocar y relleno oscuro.
 */
@Composable
fun GuildTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    leadingIcon: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AncientGold700,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = SteelSilver500,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = leadingIcon?.let {
                { Text(it, fontSize = 16.sp) }
            },
            visualTransformation = if (isPassword)
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            else
                androidx.compose.ui.text.input.VisualTransformation.None,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AncientGold,
                unfocusedBorderColor = SaddleBrown,
                focusedLeadingIconColor = AncientGold,
                unfocusedLeadingIconColor = SteelSilver500,
                focusedTextColor = SteelSilver,
                unfocusedTextColor = SteelSilver,
                cursorColor = AncientGold,
                focusedContainerColor = DungeonNoir,
                unfocusedContainerColor = DungeonNoir
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

/**
 * Caja de mensaje (éxito/error) con borde y color según estado.
 */
@Composable
fun GuildFeedback(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    if (message.isBlank()) return
    val accent = if (isError) DragonRed else DungeonGreen
    val textColor = if (isError) AmberFlame200 else AncientGold200
    val fill = if (isError) DragonRedSurface else DungeonNoir500
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(fill)
            .border(1.dp, accent, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = message,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Separador ornamental con línea dorada en degradado y rombo central.
 * Versión premium del OrnamentalDivider original.
 */
@Composable
fun GuildDivider(
    modifier: Modifier = Modifier,
    tint: Color = AncientGold
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color.Transparent, tint.copy(alpha = 0.7f), tint)
                    )
                )
        )
        Text(" ◆ ", color = tint, fontSize = 12.sp, textAlign = TextAlign.Center)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(tint, tint.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )
    }
}
