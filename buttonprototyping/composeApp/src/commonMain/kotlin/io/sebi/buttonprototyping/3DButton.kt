import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState

@Composable
fun ThreeDButton(
    buttonColor: Color = Color(0xffd736ff),
    radius: Dp = 16.dp,
    content: @Composable RowScope.() -> Unit,
    onPress: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()


    var isClicked by remember { mutableStateOf(false) }

    val offset by animateIntAsState(
        targetValue = when {
            isClicked -> 10
            isHovered -> 25
            else -> 20
        }
    )
    Box(
        Modifier
            .graphicsLayer { rotationX = 15f }
            .pointerHoverIcon(PointerIcon.Hand)
            .hoverable(interactionSource)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isClicked = true
                        onPress()
                        awaitRelease()
                        isClicked = false
                    }
                )
            }
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
    ) {

        Box(
            Modifier.fillMaxSize()
                .offset { IntOffset(0, offset) }
                .background(
                    Color.Black.copy(alpha = .3f),
                    shape = RoundedCornerShape(radius)
                )
        )

        Box(
            Modifier.fillMaxSize()
                .background(
                    color = buttonColor,
                    shape = RoundedCornerShape(radius)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = .3f),
                        ),
                    ),
                    shape = RoundedCornerShape(radius)
                )
        )

        Box(
            Modifier.fillMaxSize()
                .offset { IntOffset(0, -offset) }
                .background(
                    color = buttonColor,
                    shape = RoundedCornerShape(radius)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(
                                alpha = rememberInfiniteTransition()
                                    .animateFloat(
                                        initialValue = 0.3f,
                                        targetValue = 0.5f,
                                        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
                                    ).value
                            ),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(radius)
                )
                .border(
                    width = (1f).dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = .5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = .1f)
                        )
                    ),
                    shape = RoundedCornerShape(radius)
                )
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Row(content = content)
        }
    }
}