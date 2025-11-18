package org.oakim.img2scheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntSize
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.skia.Image
import org.oakim.img2scheme.Constants.SCHEME_FILE_EXTENSION
import org.oakim.img2scheme.Constants.SUPPORTED_IMAGE_EXTENSIONS
import org.oakim.img2scheme.components.PrintPreviewDialog
import org.oakim.img2scheme.serialization.JSON
import org.oakim.img2scheme.serialization.Scheme
import java.awt.FileDialog
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterJob
import java.io.File
import java.io.FilenameFilter
import javax.imageio.ImageIO

object ButtonHandlers {

    fun nothing(appState: AppState) {

    }

    fun print(appState: AppState) {
        appState.showPrintPreviewDialog = true
    }

    fun saveAs(appState: AppState) {
        val dialog = FileDialog(ComposeWindow(), "Save As", FileDialog.SAVE).apply {
            directory = appState.imageFileName!!.substringBeforeLast("/")
            file = appState.imageFileName!!.substringAfterLast("/")
                .let { if (it.endsWith(SCHEME_FILE_EXTENSION)) it else it + SCHEME_FILE_EXTENSION }
            isVisible = true
        }
        dialog.file?.let { name ->
            val filePath = dialog.directory + name
            File(filePath).bufferedWriter().use { bw ->
                bw.write(JSON.writeValueAsString(Scheme.fromAppState(appState)))
            }
        }
    }

    fun openFileBtnHandler(appState: AppState) {
        fun selectImage(): File? {
            appState.reset()
            val dialog = FileDialog(ComposeWindow(), "Select image", FileDialog.LOAD).apply {
                filenameFilter = FilenameFilter { _, name ->
                    SUPPORTED_IMAGE_EXTENSIONS.any { name.lowercase().endsWith(it) }
                }
                isVisible = true
            }
            return if (dialog.file != null) File(dialog.directory, dialog.file) else null
        }

        val file = selectImage()
        file?.let {
            try {
                if (file.name.endsWith(SCHEME_FILE_EXTENSION)) {
                    appState.reset()
                    val scheme: Scheme = JSON.readValue(it)
                    appState.restore(scheme)
                } else {
                    appState.imageFileName = it.absolutePath
                    val image = Image.makeFromEncoded(it.readBytes())
                    appState.imageBitmap = image.toComposeImageBitmap()
                    appState.saturation = 1f
                    appState.brightness = 0f
                    ScheduledExecutions.afterImageDraw += { zoomExpand(appState) }
                    ScheduledExecutions.afterImageDraw += { appState.calculateGridHeight() }
                    ScheduledExecutions.afterImageDraw += { appState.recalculateDisplayablePalette() }
                }
            } catch (e: IllegalArgumentException) {
                // todo: warn
                println("Wrong file format")
            }
        }
    }

    fun zoomOut(appState: AppState) {
        appState.scale *= 0.9f
    }

    fun zoomReset(appState: AppState) {
        appState.scale = 1f
        appState.offset = Offset.Zero
    }

    fun zoomExpand(appState: AppState) {
        fun calculateScale(imageSize: Size, boxSize: IntSize): Float {
            if (boxSize.width == 0 || boxSize.height == 0) return 1f

            val widthScale = boxSize.width.toFloat() / imageSize.width
            val heightScale = boxSize.height.toFloat() / imageSize.height

            return minOf(widthScale, heightScale)
        }

        appState.scale = calculateScale(appState.imageSize, appState.boxSize) * 0.95f
        appState.offset = Offset.Zero
    }

    fun zoomIn(appState: AppState) {
        appState.scale *= 1.1f
    }

}