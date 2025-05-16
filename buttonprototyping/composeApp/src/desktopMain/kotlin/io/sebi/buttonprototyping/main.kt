package io.sebi.buttonprototyping

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "buttonprototyping",
        alwaysOnTop = true
    ) {
        MyApp()
    }
}