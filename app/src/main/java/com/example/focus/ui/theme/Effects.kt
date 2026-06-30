package com.example.focus.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val GuildBorderBrush: Brush
    get() = Brush.linearGradient(
        colors = listOf(SaddleBrown, AncientGold700, SaddleBrown)
    )

val GoldEdgeBrush: Brush
    get() = Brush.linearGradient(
        colors = listOf(AncientGold700, AncientGold, AncientGold200, AncientGold, AncientGold700)
    )

val SurfaceSheenBrush: Brush
    get() = Brush.verticalGradient(
        colors = listOf(DungeonNoir500, DungeonNoir700, InkBlack)
    )

fun Modifier.guildGlow(
    color: Color = AmberFlame,
    radius: Dp = 18.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    alpha: Float = 0.55f
): Modifier = this.shadow(
    elevation = radius,
    shape = shape,
    ambientColor = color.copy(alpha = alpha),
    spotColor = color.copy(alpha = alpha)
)

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

@Composable
fun DungeonBackground(
    modifier: Modifier = Modifier,
    glowTint: Color = AmberFlame,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DungeonNoir700, DungeonNoir, InkBlack)
                )
            )
            .drawBehind {
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
