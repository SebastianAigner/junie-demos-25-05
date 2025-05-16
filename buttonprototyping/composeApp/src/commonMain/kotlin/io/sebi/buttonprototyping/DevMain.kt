package io.sebi.buttonprototyping

import ThreeDButton
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.Text
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


@Composable
fun MyApp() {
    Box(Modifier.fillMaxSize().background(Color(0xFF333333)).padding(4.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val eventFlow = remember { MutableSharedFlow<Unit>(extraBufferCapacity = 1) }
//            val element = "🔥"
            val element = "💧"
            ThreeDButton(
                buttonColor = Color(0xFF00306C),
                radius = 60.dp,
                onPress = {
                    eventFlow.tryEmit(Unit)
                },
                content = {
                    Text(element, fontSize = 80.sp)
                }
            )
            FireworksComposable(element, {
                eventFlow.asSharedFlow()
            }, modifier = Modifier.fillMaxWidth().height(40.dp))
        }
        Footer()
    }
}

@Composable
private fun BoxScope.Footer() {
    Text(
        "Component adapted from sinasamaki.com",
        fontSize = 10.sp,
        color = Color.LightGray,
        modifier = Modifier.align(Alignment.BottomEnd)
    )
}