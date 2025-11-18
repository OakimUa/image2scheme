package org.oakim.img2scheme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import org.jetbrains.skia.Image
import org.oakim.img2scheme.AppState
import org.oakim.img2scheme.image.generateGridPages
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterJob
import javax.imageio.ImageIO

@Composable
fun PrintPreviewDialog(
    appState: AppState,
    onDismiss: () -> Unit
) {
    var isPrinting by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val windowState = rememberDialogState(
        width = 1000.dp,
        height = 800.dp,
        position = WindowPosition(Alignment.Center)
    )

    val imageBitmaps = listOf<ImageBitmap>(
        appState.adjustedImageBitmap!!
    ) + generateGridPages(appState)

    DialogWindow(onCloseRequest = onDismiss, title = "Print Preview", state = windowState, resizable = false) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.background(Color(0xFFEFEFEF))
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .background(Color(0xFFEFEFEF))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            imageBitmaps.forEachIndexed { index, img ->
                                if (img.height > 0 && img.width > 0) {
                                    A4PagePreview(index + 1, imageBitmaps.size, img)
                                }
                            }
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxHeight()
                        .border(1.dp, Color.Black)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("Placeholder for config")
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Button(
                            onClick = {
                                isPrinting = true
                                printImages(imageBitmaps)
                                isPrinting = false
                                onDismiss()
                            },
                            enabled = !isPrinting
                        ) {
                            Text("Print")
                        }

                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun A4PagePreview(pageNum: Int, total: Int, image: ImageBitmap) {
    val a4Ratio = 1f / 1.4142f
    val pageWidth = 500.dp
    val pageHeight = pageWidth / a4Ratio
    val pageShape = RoundedCornerShape(8.dp)
    val imgRatio = image.width.toFloat() / image.height.toFloat()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(pageWidth)
                .height(pageHeight)
                .shadow(8.dp, pageShape, clip = false)
                .clip(pageShape)
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = BitmapPainter(image),
                contentDescription = "Page $pageNum",
                modifier = Modifier
                    .let {
                        if (imgRatio >= a4Ratio) {
                            it.fillMaxWidth()
                        } else {
                            it.fillMaxHeight()
                        }
                    }
                    .aspectRatio(imgRatio)
                    .padding(12.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text("Page $pageNum from $total", style = MaterialTheme.typography.caption)
    }
}

fun printImages(imageBitmaps: Collection<ImageBitmap>) {
    val awtImages = imageBitmaps
        .map { it.asSkiaBitmap().let { Image.makeFromBitmap(it) } }
        .map { it.encodeToData()!!.bytes }
        .map { ImageIO.read(it.inputStream()) }

    val printerJob = PrinterJob.getPrinterJob()
    printerJob.setPrintable(object : Printable {
        override fun print(g: Graphics, pf: PageFormat, pageIndex: Int): Int {
            if (pageIndex >= awtImages.size) return Printable.NO_SUCH_PAGE
            val g2d = g as Graphics2D
            val image = awtImages[pageIndex]
            val pageWidth = pf.imageableWidth
            val pageHeight = pf.imageableHeight
            val imgWidth = image.getWidth(null).toDouble()
            val imgHeight = image.getHeight(null).toDouble()
            val pageRatio = pageWidth / pageHeight
            val imgRatio = imgWidth / imgHeight
            val scale: Double
            val drawWidth: Double
            val drawHeight: Double
            if (imgRatio >= pageRatio) {
                scale = pageWidth / imgWidth
                drawWidth = pageWidth
                drawHeight = imgHeight * scale
            } else {
                scale = pageHeight / imgHeight
                drawWidth = imgWidth * scale
                drawHeight = pageHeight
            }
            val x = pf.imageableX + (pageWidth - drawWidth) / 2.0
            val y = pf.imageableY

            g2d.translate(x, y)
            g2d.drawImage(image, 0, 0, drawWidth.toInt(), drawHeight.toInt(), null)
            return Printable.PAGE_EXISTS
        }
    })

    if (printerJob.printDialog()) {
        printerJob.print()
    }
}