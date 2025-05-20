package io.sebi.dragdemo

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun makeStringFromDNDEvent(event: DragAndDropEvent, onString: (String) -> Unit): Unit = TODO()