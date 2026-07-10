package com.example.focus.ui.screen

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.focus.ui.component.GuildDivider
import com.example.focus.ui.component.GuildOutlineButton
import com.example.focus.ui.component.GuildPrimaryButton
import com.example.focus.ui.theme.AmberFlame
import com.example.focus.ui.theme.AncientGold
import com.example.focus.ui.theme.AncientGold200
import com.example.focus.ui.theme.AncientGold700
import com.example.focus.ui.theme.DungeonBackground
import com.example.focus.ui.theme.DungeonNoir700
import com.example.focus.ui.theme.InkBlack
import com.example.focus.ui.theme.SaddleBrown
import com.example.focus.ui.theme.SteelSilver500
import com.example.focus.ui.theme.guildGlow

@Composable
fun AuthEntryScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    DungeonBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Emblema del gremio animado ──────────────────────
            GuildCrest()

            Spacer(modifier = Modifier.height(28.dp))

            // ── Título grabado ──────────────────────────────────
            EngravedTitle("WardenClass")

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "EL GREMIO DEL ESTUDIO",
                style = MaterialTheme.typography.labelMedium,
                color = AncientGold700,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))
            GuildDivider(modifier = Modifier.fillMaxWidth(0.7f))
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "\"Cada hora de estudio forja un héroe.\nLa mazmorra del conocimiento te aguarda.\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = SteelSilver500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Acciones ────────────────────────────────────────
            GuildPrimaryButton(
                text = "ENTRAR A LA AVENTURA",
                leading = "⚔",
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth(),
                animatedBorder = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            GuildOutlineButton(
                text = "FORJAR NUEVA CUENTA",
                leading = "✦",
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth(),
                animatedBorder = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "v1.0 · Forjado en las profundidades",
                style = MaterialTheme.typography.labelSmall,
                color = SteelSilver500.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Emblema circular con runas giratorias y pulso de antorcha ────
@Composable
private fun GuildCrest() {
    val transition = rememberInfiniteTransition(label = "crest")
    val pulse by transition.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )
    val glow by transition.animateFloat(
        initialValue = 0.45f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )
    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(28000, easing = LinearEasing)),
        label = "rot"
    )

    Box(contentAlignment = Alignment.Center) {

        // Anillo de runas girando lentamente
        Box(
            modifier = Modifier
                .size(168.dp)
                .rotate(rotation)
                .alpha(0.35f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✦ ᚠ ✦ ᚢ ✦ ᚦ ✦ ᚨ ✦ ᚱ ✦ ᚲ ✦ ᚷ ✦",
                color = AncientGold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }

        // Disco central con borde metálico y glow ámbar pulsante
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulse)
                .guildGlow(color = AmberFlame, radius = 30.dp, shape = CircleShape, alpha = glow)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(DungeonNoir700, InkBlack)
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(SaddleBrown, AncientGold200, SaddleBrown)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚔",
                fontSize = 52.sp,
                color = AncientGold.copy(alpha = glow + 0.1f)
            )
        }
    }
}

// ── Título con efecto grabado (capa de sombra + capa dorada) ─────
@Composable
private fun EngravedTitle(text: String) {
    var textStyle by remember { mutableStateOf(androidx.compose.material3.MaterialTheme.typography.displayLarge.copy(letterSpacing = 6.sp)) }
    var readyToDraw by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.drawWithContent {
            if (readyToDraw) drawContent()
        }
    ) {
        // Sombra inferior (relieve hundido)
        Text(
            text = text,
            style = textStyle,
            color = InkBlack.copy(alpha = 0.7f),
            modifier = Modifier.offset(y = 2.dp),
            maxLines = 1,
            softWrap = false,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth) {
                    textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9f)
                } else {
                    readyToDraw = true
                }
            }
        )
        // Cara dorada
        Text(
            text = text,
            style = textStyle,
            color = AncientGold,
            maxLines = 1,
            softWrap = false
        )
    }
}
