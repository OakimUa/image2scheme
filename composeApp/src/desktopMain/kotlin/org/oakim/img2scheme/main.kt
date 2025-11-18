package org.oakim.img2scheme

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

@ExperimentalComposeUiApi
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Image2Scheme",
        state = WindowState(width = 800.dp, height = 600.dp).apply {
            isMinimized = false
            placement = WindowPlacement.Maximized
        }
    ) {
        App()
    }
}