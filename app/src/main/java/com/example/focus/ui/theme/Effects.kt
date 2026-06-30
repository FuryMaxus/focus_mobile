package com.example.focus.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar

// ── Ciclo Día/Noche: Colores según hora real ───────────────────

@Composable
fun getDungeonColors(): List<Color> {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return remember(hour) {
        when (hour) {
            in 6..18 -> listOf(DungeonNoir700, DungeonNoir, InkBlack) // Día: Cálido
            else -> listOf(Color(0xFF1A1A2E), Color(0xFF0F0F1B), Color(0xFF000000)) // Noche: Púrpura/Azul
        }
    }
}

// ── Toque Mágico: Runas que aparecen al tocar ───────────────────

/** Borde metálico estático: cuero → oro → cuero. */
val GuildBorderBrush: Brush
    get() = Brush.linearGradient(
        colors = listOf(SaddleBrown, AncientGold700, SaddleBrown)
    )

/** Borde de oro puro (resalte fuerte). */
val GoldEdgeBrush: Brush
    get() = Brush.linearGradient(
        colors = listOf(AncientGold700, AncientGold, AncientGold200, AncientGold, AncientGold700)
    )

/** Relleno sutil para superficies elevadas: da volumen a las cards. */
val SurfaceSheenBrush: Brush
    get() = Brush.verticalGradient(
        colors = listOf(DungeonNoir500, DungeonNoir700, InkBlack)
    )

// ── Formas Personalizadas de Gremio ─────────────────────────────

/** Forma de escudo heráldico con punta inferior. */
val ShieldShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height * 0.7f)
    lineTo(size.width * 0.5f, size.height)
    lineTo(0f, size.height * 0.7f)
    close()
}

// ── Glow: resplandor radial puro ───────────────────────────────

/**
 * Aplica un resplandor de [color] alrededor del elemento.
 * Usa drawBehind para un efecto de luz suave y orgánico, evitando
 * la sombra "cuadrada" de elevación estándar.
 */
fun Modifier.guildGlow(
    color: Color = AmberFlame,
    radius: Dp = 18.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    alpha: Float = 0.55f
): Modifier = this.drawBehind {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = size.maxDimension * 0.8f + radius.toPx()
        ),
        radius = size.maxDimension * 0.8f + radius.toPx(),
        center = center
    )
}

// ── Borde dorado animado (shimmer recorriendo el contorno) ──────

/**
 * Borde con un brillo dorado que se desliza de lado a lado,
 * dando sensación de metal pulido bajo una antorcha.
 */
@Composable
fun Modifier.animatedGoldBorder(
    width: Dp = 1.5.dp,
    shape: Shape = RoundedCornerShape(10.dp),
    baseColor: Color = SaddleBrown
): Modifier {
    val transition = rememberInfiniteTransition(label = "goldShimmer")
    val offset by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            AncientGold700,
            AncientGold200,
            AncientGold,
            AncientGold700,
            baseColor
        ),
        start = Offset(offset * 600f - 300f, 0f),
        end = Offset(offset * 600f + 300f, 220f),
        tileMode = TileMode.Clamp
    )

    return this.border(width = width, brush = brush, shape = shape)
}

// ── Toque Mágico: Runas que aparecen al tocar ───────────────────

data class MagicRune(
    val id: Long,
    val x: Float,
    val y: Float,
    val text: String,
    val color: Color
)

@Composable
fun MagicTouchWrapper(content: @Composable () -> Unit) {
    val runes = remember { mutableStateListOf<MagicRune>() }
    val runeSymbols = listOf("ᚠ", "ᚢ", "ᚦ", "ᚨ", "ᚱ", "ᚲ", "ᚷ", "ᚹ", "ᚺ", "ᚾ", "✦")
    val colors = listOf(AncientGold, AmberFlame, AncientGold200)
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Usamos awaitPointerEventScope para detectar toques sin bloquear el scroll ni botones
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == androidx.compose.ui.input.pointer.PointerEventType.Press) {
                            val change = event.changes.first()
                            val offset = change.position
                            val newRune = MagicRune(
                                id = System.currentTimeMillis() + (0..10000).random(),
                                x = offset.x,
                                y = offset.y,
                                text = runeSymbols.random(),
                                color = colors.random()
                            )
                            runes.add(newRune)
                        }
                    }
                }
            }
    ) {
        content()

        runes.forEach { rune ->
            key(rune.id) {
                val transition = rememberInfiniteTransition(label = "runeAnim")
                
                val alpha by transition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "alpha"
                )
                
                val scale by transition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 2.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = EaseOutExpo),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "scale"
                )

                LaunchedEffect(rune.id) {
                    delay(1100)
                    runes.remove(rune)
                }

                Text(
                    text = rune.text,
                    color = rune.color.copy(alpha = alpha),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(
                            x = with(density) { (rune.x).toDp() } - 14.dp,
                            y = with(density) { (rune.y).toDp() } - 14.dp
                        )
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            rotationZ = alpha * 45f
                        }
                )
            }
        }
    }
}

// ── Fondo de mazmorra: gradiente vertical + viñeta radial ───────

@Composable
fun DungeonBackground(
    modifier: Modifier = Modifier,
    glowTint: Color = AmberFlame,
    content: @Composable () -> Unit
) {
    val bgColors = getDungeonColors()
    
    MagicTouchWrapper {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = bgColors)
                )
                .drawBehind {
                    // Resplandor cálido de antorcha hacia el centro-superior
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowTint.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.22f),
                            radius = size.maxDimension * 0.55f
                        )
                    )
                    // Viñeta: oscurece esquinas para dar foco
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                InkBlack.copy(alpha = 0.55f)
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.5f),
                            radius = size.maxDimension * 0.75f
                        )
                    )
                }
        ) {
            content()
        }
    }
}
