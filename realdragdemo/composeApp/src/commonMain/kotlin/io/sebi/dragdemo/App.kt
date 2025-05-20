package io.sebi.dragdemo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import dragdemo.composeapp.generated.resources.Res
import dragdemo.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    DragAndDropApp()
}

@Composable
private fun DragAndDropApp() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top box with three fruit emoji
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "🍎", fontSize = 80.sp, modifier = Modifier.dragAndDropSource(transferData = {
                            makeDNDDataFromString("Compose Multiplatform for iOS")
                        })
                    )
                    Text("🤖", fontSize = 80.sp, modifier = Modifier.dragAndDropSource(transferData = {
                        makeDNDDataFromString("Compose Multiplatform for Android")
                    }))
                    Text("💻", fontSize = 80.sp, modifier = Modifier.dragAndDropSource(transferData = {
                        makeDNDDataFromString("Compose Multiplatform for Desktop")
                    }))
                    //VideoPlayer("https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4", Modifier.fillMaxSize())
                }
            }

            // Bottom box with light gray background
            var selectedFruit by remember { mutableStateOf("") }
            val callback = remember {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        makeStringFromDNDEvent(event) {
                            selectedFruit = it
                        }
                        return true
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color(0xFFE6E6FA))
                    .dragAndDropTarget(
                        shouldStartDragAndDrop = { true },
                        target = callback
                    )
                    .padding(30.dp)
            ) {
                // Empty box with light gray background
                Text(
                    selectedFruit,
                    fontSize = 30.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.2).sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

expect fun makeDNDDataFromString(s: String): DragAndDropTransferData

expect fun makeStringFromDNDEvent(event: DragAndDropEvent, onString: (String) -> Unit): Unit