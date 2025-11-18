package org.oakim.img2scheme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import org.oakim.img2scheme.image.Cell
import org.oakim.img2scheme.serialization.Scheme
import java.awt.Point

interface AppState {
    // toggles
    var showGrid: Boolean
    var applyPalette: Boolean
    val imageSelected: Boolean
    var showPrintPreviewDialog: Boolean

    // image
    // - original image
    var imageBitmap: ImageBitmap?
    var imageFileName: String?
    var brightness: Float
    var saturation: Float
    var contrast: Float
    val adjustedImageBitmap: ImageBitmap?

    // zoom and positioning
    var scale: Float
    var offset: Offset
    var imageSize: Size
    var boxSize: IntSize
    var lastCursor: Offset
    var imgCursor: Offset
    var colorUnderCursor: Color
    var cellUnderCursor: Point

    // palette
    var colorSelectionTarget: Int?
    val displayablePalette: Map<Int, Color>
    var drawColorNum: Int?

    // grid
    var selectedBeadRatio: Int
    var selectedStitchType: String
    var gridWidth: Int
    var gridHeight: Int
    var gridState: String
    val grid: Map<Pair<Int, Int>, Cell>

    // Methods
    fun reset()
    fun calculateGridWidth()
    fun calculateGridHeight()
    fun addColorToPalette(key: Int, color: Color)
    fun removeColorFromPalette(key: Int)
    fun recalculateDisplayablePalette()
    fun restore(scheme: Scheme)
    fun setGridColor(x: Int, y: Int, color: Color)
}