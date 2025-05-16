package io.sebi.buttonprototyping

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.Flow

data class FireworkAnimation(
    val id: Int,
)

@Composable
fun FireworksComposable(
    element: String = "🔥",
    onEvent: () -> Flow<Unit>,
    modifier: Modifier = Modifier
) {
    var animations by remember { mutableStateOf(listOf<FireworkAnimation>()) }
    var nextId by remember { mutableStateOf(0) }
    var composableWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    // Listen for new events
    LaunchedEffect(Unit) {
        onEvent().collect {
            println("event!")
            animations = animations + FireworkAnimation(nextId++)
            println(animations)
        }
    }

    Box(modifier = modifier.onSizeChanged { composableWidth = with(density) { it.width.toDp() } }) {

        animations.forEach { anim ->
            key(anim.id) {
                val animationProgress = remember { Animatable(0f) }

                LaunchedEffect(anim.id) {
                    animationProgress.animateTo(
                        targetValue = 1.2f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = LinearEasing
                        )
                    )
                }

                val xOffset = (composableWidth * (1f - animationProgress.value))

                Box(
                    modifier = Modifier.offset(x = xOffset).size(40.dp).background(
                        color = Color.White, shape = CircleShape
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        element,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
