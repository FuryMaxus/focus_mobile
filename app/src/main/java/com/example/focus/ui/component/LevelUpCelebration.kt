package com.example.focus.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focus.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LevelUpCelebration(level: Int, onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(true) }
    
    val transition = rememberInfiniteTransition(label = "celebration")
    val scale by transition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOutBack), RepeatMode.Reverse),
        label = "scale"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        delay(4000) // Se muestra por 4 segundos
        visible = false
        onDismiss()
    }

    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .guildGlow(color = AncientGold, radius = 50.dp, alpha = glowAlpha),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.scale(scale)
            ) {
                Text(
                    text = "¡NIVEL ALCANZADO!",
                    style = MaterialTheme.typography.displayMedium,
                    color = AncientGold,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.radialGradient(listOf(AncientGold, Color.Transparent)),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$level",
                        fontSize = 64.sp,
                        color = InkBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Tu leyenda continúa...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = SteelSilver,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}
