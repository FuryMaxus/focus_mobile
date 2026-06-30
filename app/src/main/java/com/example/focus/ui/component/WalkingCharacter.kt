package com.example.focus.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.focus.R

enum class GuildCharacter(
    @param:DrawableRes val resId: Int,
    val displayName: String
) {
    Duende(R.drawable.char_duende_verde, "Duende Verde"),
    Demonio(R.drawable.char_demonio_morado, "Demonio Morado"),
    Dragon(R.drawable.char_dragon_rojo, "Dragón Rojo"),
    Cavernicola(R.drawable.char_cavernicola, "Cavernícola");

    companion object {
        fun fromName(name: String?): GuildCharacter =
            entries.firstOrNull { it.name == name } ?: Duende
    }
}

enum class CharacterPose { Idle, Walk }

@Composable
fun AnimatedCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    pose: CharacterPose = CharacterPose.Idle,
    stroll: Boolean = false
) {
    AnimatedImage(
        resId = character.resId,
        modifier = modifier,
        size = size,
        pose = pose,
        stroll = stroll,
        contentDescription = character.displayName
    )
}

@Composable
fun AnimatedImage(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    pose: CharacterPose = CharacterPose.Idle,
    stroll: Boolean = false,
    contentDescription: String? = null
) {
    when (pose) {
        CharacterPose.Idle -> IdleAnimation(resId, modifier, size, contentDescription)
        CharacterPose.Walk -> WalkingAnimation(resId, modifier, size, stroll, contentDescription)
    }
}

@Composable
private fun IdleAnimation(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    contentDescription: String? = null
) {
    val transition = rememberInfiniteTransition(label = "idle")
    val sway by transition.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sway"
    )
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "idleBob"
    )
    val breathe by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breathe"
    )

    Image(
        painter = painterResource(id = resId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            .offset(y = bob.dp)
            .graphicsLayer {
                rotationZ = sway
                scaleX = 1f + 0.03f * breathe
                scaleY = 1f - 0.03f * breathe
            }
    )
}

@Composable
private fun WalkingAnimation(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    stroll: Boolean = false,
    contentDescription: String? = null
) {
    val transition = rememberInfiniteTransition(label = "walk")
    val rotation by transition.animateFloat(
        initialValue = -6.5f,
        targetValue = 6.5f,
        animationSpec = infiniteRepeatable(tween(1066, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "rotation"
    )
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(533, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bob"
    )

    val painter = painterResource(id = resId)

    if (stroll) {
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
            label = "stroll"
        )
        val goingRight = phase < 0.5f
        val travel = if (goingRight) phase * 2f else (1f - phase) * 2f
        val bias = travel * 2f - 1f

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(size + 16.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(BiasAlignment(horizontalBias = bias, verticalBias = 1f))
                    .size(size)
                    .offset(y = bob.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        scaleX = if (goingRight) 1f else -1f
                    }
            )
        }
    } else {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .size(size)
                .offset(y = bob.dp)
                .graphicsLayer { rotationZ = rotation }
        )
    }
}

@Composable
fun IdleCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    IdleAnimation(character.resId, modifier, size, character.displayName)
}

@Composable
fun WalkingCharacter(
    character: GuildCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    stroll: Boolean = false
) {
    WalkingAnimation(character.resId, modifier, size, stroll, character.displayName)
}
