package org.oakim.img2scheme.tiles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import org.oakim.img2scheme.AppState
import org.oakim.img2scheme.Constants.COLOR_DEFAULT
import org.oakim.img2scheme.ScheduledExecutions
import org.oakim.img2scheme.executeAll
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage

@ExperimentalComposeUiApi
@Composable
fun ContentTile(appState: AppState) =
    with(appState) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(end = 10.dp, bottom = 10.dp)
                .background(color = Color.White)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .onGloballyPositioned { coordinates ->
                    boxSize = coordinates.size
                }
                .onPointerEvent(PointerEventType.Move) { event ->
                    lastCursor = event.changes.firstOrNull()?.position ?: lastCursor
                    val mousePos = MouseInfo.getPointerInfo().location
                    val robot = Robot()
                    val captureRect = Rectangle(mousePos.x, mousePos.y, 1, 1)
                    val screenshot: BufferedImage = robot.createScreenCapture(captureRect)
                    colorUnderCursor = Color(screenshot.getRGB(0, 0))
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val scrollEvent = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                            if (scrollEvent != 0f) {
                                val scaleFactor = if (scrollEvent < 0) 1.02f else 0.98f
                                scale *= scaleFactor
                                offset += (lastCursor - offset - boxSize.let {
                                    Offset(
                                        it.width.toFloat() / 2,
                                        it.height.toFloat() / 2
                                    )
                                }) * (1 - scaleFactor)
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { _ ->
                        if (colorSelectionTarget != null) {
                            addColorToPalette(colorSelectionTarget!!, colorUnderCursor)
                        }
                        if (drawColorNum != null) {
                            setGridColor(cellUnderCursor.x, cellUnderCursor.y, displayablePalette[drawColorNum]!!)
                        }
                    })
                },
            contentAlignment = Alignment.Center
        ) {
            if (showGrid) {
                Canvas(
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                ) {
                    grid.values.forEach { cell ->
                        try {
                            drawRoundRect(
                                color = if (applyPalette) cell.paletteColor else cell.color,
                                topLeft = cell.offset,
                                size = cell.size,
                                cornerRadius = CornerRadius(cell.size.width / 3, cell.size.height / 3),
                                style = Fill
                            )
                            drawRoundRect(
                                color = Color.DarkGray,
                                topLeft = cell.offset,
                                size = cell.size,
                                cornerRadius = CornerRadius(cell.size.width / 3, cell.size.height / 3),
                                style = Stroke(width = 1f)
                            )
                        } catch (e: Exception) {
                            println("${cell}: ${e.message}")
                        }
                    }
                    if (drawColorNum != null) {
                        grid[cellUnderCursor.x to cellUnderCursor.y]?.also { selectedCell ->
                            drawRoundRect(
                                color = (displayablePalette[drawColorNum] ?: COLOR_DEFAULT),
                                topLeft = selectedCell.offset,
                                size = selectedCell.size,
                                cornerRadius = CornerRadius(0f, 0f),
                                style = Fill
                            )
                            drawRoundRect(
                                color = Color.DarkGray,
                                topLeft = selectedCell.offset,
                                size = selectedCell.size,
                                cornerRadius = CornerRadius(2f, 2f),
                                style = Stroke(width = 1.5f)
                            )
                        }
                    }
                }
            } else {
                adjustedImageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Opened Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                imageSize = Size(
                                    coordinates.size.width.toFloat(),
                                    coordinates.size.height.toFloat()
                                )
                                ScheduledExecutions.afterImageDraw.executeAll()
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .onPointerEvent(PointerEventType.Move) { event ->
                                event.changes.firstOrNull()?.position?.let { position ->
                                    imgCursor = position - imageSize.let { Offset(it.width, it.height) }.div(2f)
                                    grid.entries.firstOrNull { (_, cell) -> cell.offset.x <= imgCursor.x && imgCursor.x <= (cell.offset.x + cell.size.width)  &&
                                            cell.offset.y <= imgCursor.y && imgCursor.y <= (cell.offset.y + cell.size.height) }
                                        ?.key?.let { cellUnderCursor = Point(it.first, it.second) }
                                }
                            }
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, _, _ ->
                                    offset += pan.times(scale)
                                }
                            }
                    )
                }
            }
        }
    }
