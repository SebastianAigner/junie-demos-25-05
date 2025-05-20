package io.sebi.dragdemo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.uikit.fromString
import androidx.compose.ui.uikit.loadString
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.viewinterop.UIKitViewController
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSURL
import platform.UIKit.UIDevice
import platform.UIKit.UIDragItem

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalComposeUiApi::class)
actual fun makeDNDDataFromString(s: String): DragAndDropTransferData {
    return DragAndDropTransferData(listOf(UIDragItem.fromString(s)))
}

@Composable
actual fun VideoPlayer(url: String, modifier: Modifier) {
    UIKitViewController(
        factory = {
            val player = AVPlayer(uRL = NSURL(string = url))
            val controller = AVPlayerViewController()
            controller.player = player
            player.play()
            controller
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun makeStringFromDNDEvent(event: DragAndDropEvent, onString: (String) -> Unit): Unit {
    var str: String
    event.items.first().loadString { s, e ->
        if (s != null) {
            onString(s)
        }
    }
}
