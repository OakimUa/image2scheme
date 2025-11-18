package org.oakim.img2scheme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.oakim.img2scheme.Constants.BEAD_RATIO_BASE
import org.oakim.img2scheme.Constants.BEAD_RATIO_OVAL
import org.oakim.img2scheme.Constants.BRIGHTNESS_NORMAL
import org.oakim.img2scheme.Constants.COLOR_DEFAULT
import org.oakim.img2scheme.Constants.CONTRAST_NORMAL
import org.oakim.img2scheme.Constants.GRID_FIXED
import org.oakim.img2scheme.Constants.GRID_FREE
import org.oakim.img2scheme.Constants.SATURATION_NORMAL
import org.oakim.img2scheme.Constants.STITCH_TYPE_BRICK
import org.oakim.img2scheme.Constants.STITCH_TYPE_PEYOTE
import org.oakim.img2scheme.Constants.STITCH_TYPE_SQUARE
import org.oakim.img2scheme.Constants.backingGrid
import org.oakim.img2scheme.components.PrintPreviewDialog
import org.oakim.img2scheme.image.Cell
import org.oakim.img2scheme.image.adjustImageBrightness
import org.oakim.img2scheme.image.adjustImageContrastGamma
import org.oakim.img2scheme.image.adjustImageSaturation
import org.oakim.img2scheme.image.calculateColor
import org.oakim.img2scheme.image.colorDistance
import org.oakim.img2scheme.serialization.Scheme
import org.oakim.img2scheme.tiles.ContentTile
import org.oakim.img2scheme.tiles.MenuTile
import org.oakim.img2scheme.tiles.ToolPanelTile
import java.awt.Point

@ExperimentalComposeUiApi
@Composable
@Preview
fun App() {
    val appState: AppState = object : AppState {
        // toggles
        override var showGrid by remember { mutableStateOf(false) }
        override var applyPalette by remember { mutableStateOf(false) }
        override var showPrintPreviewDialog by remember { mutableStateOf(false) }

        // image
        // - original image
        override var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        override var imageFileName by remember { mutableStateOf<String?>(null) }
        override var imageSelected by remember(imageBitmap) {
            mutableStateOf(imageBitmap != null)
        }

        // - brightness adjustment
        override var brightness by remember { mutableStateOf(BRIGHTNESS_NORMAL) }
        private var adjustedImageBrightnessBitmap by remember(imageBitmap, brightness) {
            mutableStateOf(adjustImageBrightness(imageBitmap, brightness))
        }

        // - saturation adjustment
        override var saturation by remember { mutableStateOf(SATURATION_NORMAL) }
        private var adjustedImageSaturationBitmap by remember(
            adjustedImageBrightnessBitmap,
            saturation
        ) {
            mutableStateOf(adjustImageSaturation(adjustedImageBrightnessBitmap, saturation))
        }

        // - contrast adjustment
        override var contrast by remember { mutableStateOf(CONTRAST_NORMAL) }
        override var adjustedImageBitmap by remember(adjustedImageSaturationBitmap, contrast) {
            mutableStateOf(adjustImageContrastGamma(adjustedImageSaturationBitmap, contrast))
        }

        // zoom and positioning
        override var scale by remember { mutableStateOf(1f) }
        override var offset by remember { mutableStateOf(Offset.Zero) }
        override var imageSize by remember { mutableStateOf(Size.Zero) }
        override var boxSize by remember { mutableStateOf(IntSize.Zero) }
        override var lastCursor by remember { mutableStateOf(Offset.Zero) }
        override var imgCursor by remember { mutableStateOf(Offset.Zero) }
        override var colorUnderCursor by remember { mutableStateOf(COLOR_DEFAULT) }
        override var cellUnderCursor by remember { mutableStateOf(Point(0, 0)) }

        // palette
        override var colorSelectionTarget by remember { mutableStateOf<Int?>(null) }
        override var drawColorNum by remember { mutableStateOf<Int?>(null) }
        private val palette = remember {
            mutableMapOf<Int, Color>().apply {
                put(0, COLOR_DEFAULT)
            }
        }
        override var displayablePalette by remember { mutableStateOf(mapOf<Int, Color>()) }

        // grid
        override var selectedBeadRatio by remember { mutableStateOf(BEAD_RATIO_OVAL) } // h/w = X/3
        override var selectedStitchType by remember { mutableStateOf(STITCH_TYPE_SQUARE) }
        override var gridWidth by remember { mutableStateOf(100) }
        override var gridHeight by remember { mutableStateOf(1) }
        override var gridState by remember { mutableStateOf(GRID_FREE) }
        private var redrawTrigger by remember { mutableStateOf(0) }
        private var dirtyGrid by remember { mutableStateOf(false) }
        override var grid: Map<Pair<Int, Int>, Cell> by remember(
            adjustedImageBitmap,
            gridWidth,
            gridHeight,
            showGrid,
            applyPalette,
            redrawTrigger,
            dirtyGrid
        ) {
            mutableStateOf(recalculateGrid(adjustedImageBitmap, gridWidth, gridHeight))
        }

        // Methods
        override fun recalculateDisplayablePalette() {
            recalculateGrid(adjustedImageBitmap, gridWidth, gridHeight)
            displayablePalette = mutableMapOf<Int, Color>().apply { putAll(palette) }
        }

        override fun removeColorFromPalette(key: Int) {
            palette -= key
            var next = key
            while (++next in palette) {
                palette[next - 1] = palette[next]!!
                palette -= next
            }
            recalculateDisplayablePalette()
        }

        override fun addColorToPalette(key: Int, color: Color) {
            palette[key] = color
            colorSelectionTarget = null
            recalculateDisplayablePalette()
        }


        override fun calculateGridWidth() {
            val width = imageSize.width + 1
            val height = imageSize.height + 1
            val hSize = (height - height / gridHeight) / (gridHeight - 1)
            val wSize = when (selectedStitchType) {
                STITCH_TYPE_SQUARE, STITCH_TYPE_PEYOTE -> hSize * BEAD_RATIO_BASE / selectedBeadRatio
                STITCH_TYPE_BRICK -> hSize * selectedBeadRatio / BEAD_RATIO_BASE
                else -> throw RuntimeException("Not implemented stitch type: $selectedStitchType")
            }
            val newWidth: Int = (width / wSize).toInt() + 1
            if (newWidth != gridWidth) {
                gridWidth = newWidth
                gridState = GRID_FREE
            }
            dirtyGrid = !dirtyGrid
        }

        override fun calculateGridHeight() {
            val width = imageSize.width + 1
            val height = imageSize.height + 1
            val wSize = (width - width / gridWidth) / (gridWidth - 1)
            val hSize = when (selectedStitchType) {
                STITCH_TYPE_SQUARE, STITCH_TYPE_PEYOTE -> wSize * selectedBeadRatio / BEAD_RATIO_BASE
                STITCH_TYPE_BRICK -> wSize * BEAD_RATIO_BASE / selectedBeadRatio
                else -> throw RuntimeException("Not implemented stitch type: $selectedStitchType")
            }
            val newHeight = (height / hSize).toInt() + 1
            if (newHeight != gridHeight) {
                gridHeight = newHeight
                gridState = GRID_FREE
            }
            dirtyGrid = !dirtyGrid
        }

        override fun reset() {
            showGrid = false
            applyPalette = false
            imageBitmap = null
            imageFileName = null
            scale = 1f
            offset = Offset.Zero
            boxSize = IntSize.Zero
            imageSize = Size.Zero
            selectedBeadRatio = BEAD_RATIO_OVAL
            gridWidth = 100
            brightness = BRIGHTNESS_NORMAL
            saturation = SATURATION_NORMAL
            contrast = CONTRAST_NORMAL
            colorSelectionTarget = null
            palette.clear()
            palette[0] = COLOR_DEFAULT
            backingGrid.clear()
        }

        fun recalculateGrid(
            imageBitmap: ImageBitmap?,
            gridWidth: Int,
            gridHeight: Int
        ): Map<Pair<Int, Int>, Cell> {
            val width = imageSize.width + 2
            val height = imageSize.height + 2
            val wStep = (width - width / gridWidth) / (gridWidth - 1)
            val hStep = (height - height / gridHeight) / (gridHeight - 1)
            return (1..gridWidth).map { i ->
                (1..gridHeight).map { j ->
                    val offset = when (selectedStitchType) {
                        STITCH_TYPE_SQUARE -> Offset(
                            x = (-width / 2) + wStep * (i - 1),
                            y = (-height / 2) + hStep * (j - 1)
                        )
                        STITCH_TYPE_PEYOTE -> Offset(
                            x = (-width / 2) + wStep * (i - 1),
                            y = (-height / 2) + hStep * (j - 1) + (if (i % 2 == 0) hStep / 2 else 0f)
                        )
                        STITCH_TYPE_BRICK -> Offset(
                            x = (-width / 2) + wStep * (i - 1) + (if (j % 2 == 0) wStep / 2 else 0f),
                            y = (-height / 2) + hStep * (j - 1)
                        )
                        else -> throw RuntimeException("Not implemented stitch type: $selectedStitchType")
                    }
                    val size = Size(wStep, hStep)
                    val color = calculateColor(imageBitmap, offset, size)
                    val paletteColor =  if (gridState == GRID_FREE)
                        if (color.alpha < 0.5)
                                palette[0]!!
                            else
                                palette.values.minBy { colorDistance(it, color) }
                    else
                            backingGrid[i to j]?.paletteColor ?: COLOR_DEFAULT
                    (i to j) to Cell(
                        offset = offset,
                        size = size,
                        color = color,
                        paletteColor = paletteColor)
                }
            }.flatten().toMap().also {
                backingGrid.clear()
                backingGrid += it
            }
        }

        override fun restore(scheme: Scheme) {
            applyPalette = false
            imageBitmap = scheme.imageBitmap
            imageFileName = scheme.imageFileName
            brightness = scheme.brightness
            saturation = scheme.saturation
            contrast = scheme.contrast
            scale = scheme.scale
            offset = scheme.offset
            palette.putAll(scheme.palette)
            selectedBeadRatio = scheme.selectedBeadRatio
            gridWidth = scheme.gridWidth
            gridHeight = scheme.gridHeight
            grid = scheme.grid
            backingGrid += scheme.grid
            showGrid = false
            gridState = scheme.gridState
            selectedStitchType = scheme.stitchType
            recalculateDisplayablePalette()
        }

        override fun setGridColor(x: Int, y: Int, color: Color) {
            println("Set color [$x,$y]=(${palette.entries.firstOrNull { it.value == color }?.key})$color")
            gridState = GRID_FIXED
            backingGrid[cellUnderCursor.x to cellUnderCursor.y]?.apply {
                this.paletteColor = color
                println(this)
            }
            redrawTrigger++
//            recalculateGrid(adjustedImageBitmap, gridWidth, gridHeight)
        }

    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuTile(appState)
            Row {
                AnimatedVisibility(appState.imageSelected) {
                    ToolPanelTile(appState)
                }
                AnimatedVisibility(appState.imageSelected) {
                    ContentTile(appState)
                }
            }
        }
    }
    if (appState.showPrintPreviewDialog) {
        PrintPreviewDialog(appState) { appState.showPrintPreviewDialog = false }
    }
}