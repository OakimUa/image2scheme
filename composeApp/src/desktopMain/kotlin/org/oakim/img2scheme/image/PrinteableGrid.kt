package org.oakim.img2scheme.image

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.TextLine
import org.oakim.img2scheme.AppState
import org.oakim.img2scheme.Constants.BEAD_RATIO_BASE
import org.oakim.img2scheme.Constants.STITCH_TYPE_BRICK
import org.oakim.img2scheme.Constants.STITCH_TYPE_PEYOTE
import org.oakim.img2scheme.Constants.STITCH_TYPE_SQUARE
import org.oakim.img2scheme.PrintGraphicsUtils
import org.oakim.img2scheme.PrintGraphicsUtils.baseDimSize
import org.oakim.img2scheme.PrintGraphicsUtils.headerFont
import org.oakim.img2scheme.PrintGraphicsUtils.lineWidth
import kotlin.math.ceil
import kotlin.math.min

fun generateGridPages(
    appState: AppState,
    maxCellsPerPage: Int = 50
): List<ImageBitmap> {
    val scale = 1

    val grid = appState.grid
    val gridWidth = appState.gridWidth
    val gridHeight = appState.gridHeight

    val result = mutableListOf<ImageBitmap>()
    val cellDim = Size(
        when (appState.selectedStitchType) {
            STITCH_TYPE_BRICK -> baseDimSize * appState.selectedBeadRatio / BEAD_RATIO_BASE
            else -> baseDimSize
        },
        when (appState.selectedStitchType) {
            STITCH_TYPE_SQUARE, STITCH_TYPE_PEYOTE -> baseDimSize * appState.selectedBeadRatio / BEAD_RATIO_BASE
            else -> baseDimSize
        }
    )

    // full picture first
    result += generateImageBitmapByCellset(
        gridWidth,
        gridHeight,
        0,
        0,
        cellDim,
        scale,
        grid,
        appState.selectedStitchType
    )

    // and pages
    result += grid.mapKeys { (index, _) ->
        when (appState.selectedStitchType) {
            STITCH_TYPE_SQUARE -> index
            STITCH_TYPE_BRICK -> (index.second to index.first).let { index ->
                ceil(index.first.toFloat() / 2).toInt() to ((index.second - 1) * 2 + ((index.first + 1) % 2 + 1))
            }
            STITCH_TYPE_PEYOTE -> ceil(index.first.toFloat() / 2).toInt() to ((index.second - 1) * 2 + ((index.first + 1) % 2 + 1))
            else -> error("unknown stitch type: ${appState.selectedStitchType}")
        }
    }.let { mappedGrid ->
        val gw = mappedGrid.keys.maxOf { it.first }
        val gh = mappedGrid.keys.maxOf { it.second }
        val pageCols = min(gw, maxCellsPerPage)
        val pageRows = min(gh, maxCellsPerPage)
        val pagesX = ceil(gw / pageCols.toFloat()).toInt()
        val pagesY = ceil(gh / pageRows.toFloat()).toInt()
        (1..pagesX).map { px ->
            (1..pagesY).map { py ->
                generateImageBitmapByCellset(
                    maxCellsPerPage,
                    maxCellsPerPage,
                    (px - 1) * maxCellsPerPage,
                    (py - 1) * maxCellsPerPage,
                    if (appState.selectedStitchType != STITCH_TYPE_BRICK) cellDim else Size(cellDim.height, cellDim.width),
                    scale,
                    mappedGrid.filterKeys { (col, row) ->
                        col >= (px - 1) * maxCellsPerPage &&
                                col <= px * maxCellsPerPage &&
                                row >= (py - 1) * maxCellsPerPage &&
                                row <= py * maxCellsPerPage
                    },
                    tileType = when (appState.selectedStitchType) {
                        STITCH_TYPE_SQUARE -> STITCH_TYPE_SQUARE
                        STITCH_TYPE_BRICK -> STITCH_TYPE_BRICK
                        STITCH_TYPE_PEYOTE -> STITCH_TYPE_BRICK
                        else -> error("unknown stitch type: ${appState.selectedStitchType}")
                    },
                    withLabels = true,
                    colorNumber = { color ->
                        appState.displayablePalette.filterValues { it == color }.keys.firstOrNull()
                            ?: -1
                    },
                    pageIndex = "$px/$py"
                )
            }
        }.flatten()
    }

    return result
}

private fun generateImageBitmapByCellset(
    gridWidth: Int,
    gridHeight: Int,
    dW: Int = 1,
    dH: Int = 1,
    cellDim: Size,
    scale: Int,
    cells: Map<Pair<Int, Int>, Cell>,
    tileType: String = STITCH_TYPE_SQUARE,
    withLabels: Boolean = false,
    colorNumber: (Color) -> Int = { -1 },
    pageIndex: String = ""
): ImageBitmap {
    val w =
        ((gridWidth + 1 + (if (tileType == STITCH_TYPE_BRICK) 0.5f else 0.0f)) * cellDim.width * scale).toInt()
    val h =
        ((gridHeight + 1 + (if (tileType == STITCH_TYPE_PEYOTE) 0.5f else 0.0f)) * cellDim.height * scale).toInt()
    val bitmap = ImageBitmap(w, h)
    val canvas = Canvas(bitmap)
    val radiusX = cellDim.width / 3
    val radiusY = cellDim.height / 3

    val defCell = Cell(Offset(0.0f, 0.0f), Size(0.0f, 0.0f), Color.Transparent, Color.Transparent)

    fun drawCell(x: Int, y: Int, cell: Cell) {
        canvas.drawRoundRect(
            cellDim.width * x + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0) cellDim.width / 2 else 0.0f,
            cellDim.height * y + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0) cellDim.height / 2 else 0.0f,
            cellDim.width * (x + 1) + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0) cellDim.width / 2 else 0.0f,
            cellDim.height * (y + 1) + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0) cellDim.height / 2 else 0.0f,
            radiusX, radiusY,
            Paint().apply {
                color = cell.paletteColor
                alpha = cell.paletteColor.alpha
                style = PaintingStyle.Fill
            }
        )
        canvas.drawRoundRect(
            cellDim.width * x + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0) cellDim.width / 2 else 0.0f,
            cellDim.height * y + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0) cellDim.height / 2 else 0.0f,
            cellDim.width * (x + 1) + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0) cellDim.width / 2 else 0.0f,
            cellDim.height * (y + 1) + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0) cellDim.height / 2 else 0.0f,
            radiusX, radiusY,
            Paint().apply {
                color = Color.Gray
                alpha = cell.paletteColor.alpha
                style = PaintingStyle.Stroke
                strokeWidth = 3.0f
            }
        )
        if (withLabels) {
            val colorNumberInPalette = colorNumber(cell.paletteColor)
            if (colorNumberInPalette >= 0) {
                val skiaCanvas: org.jetbrains.skia.Canvas = canvas.nativeCanvas
                val line = PrintGraphicsUtils.textLine(colorNumberInPalette)
                val paint = org.jetbrains.skia.Paint().apply {
                    color = mostContrastingBW(cell.paletteColor)
                    isAntiAlias = true
                }
                val textWidth =
                    PrintGraphicsUtils.font.measureTextWidth(colorNumberInPalette.toString(), paint)
                skiaCanvas.drawTextLine(
                    line,
                    cellDim.width * (x.toFloat() + 0.5f) - textWidth / 2 + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0) cellDim.width / 2 else 0.0f,
                    cellDim.height * (y.toFloat() + 1f) + (PrintGraphicsUtils.font.metrics.ascent - PrintGraphicsUtils.font.metrics.descent) / 2 + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0) cellDim.height / 2 else 0.0f,
                    paint
                )
            }
        }
    }

    fun drawGrid(x: Int, y: Int, a: Float) {
        when (x % 10) {
            5, 0 -> canvas.drawLine(
                Offset(
                    cellDim.width * (x + 1) + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0 && y != 0) cellDim.width / 2 else 0.0f,
                    cellDim.height * y + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0 && x != 0) cellDim.height / 2 else 0.0f,
                ),
                Offset(
                    cellDim.width * (x + 1) + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0 && y != 0) cellDim.width / 2 else 0.0f,
                    cellDim.height * (y + 1) + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0 && x != 0) cellDim.height / 2 else 0.0f,
                ),
                Paint().apply {
                    color = if (x % 10 == 5) Color.DarkGray else Color.Black
                    strokeWidth = lineWidth
                    alpha = a
                }
            )
        }
        when (y % 10) {
            5, 0 -> canvas.drawLine(
                Offset(
                    cellDim.width * x + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0 && y != 0) cellDim.width / 2 else 0.0f,
                    cellDim.height * (y + 1) + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0 && x != 0) cellDim.height / 2 else 0.0f,
                ),
                Offset(
                    cellDim.width * (x + 1) + if (tileType == STITCH_TYPE_BRICK && y % 2 == 0 && y != 0) cellDim.width / 2 else 0.0f,
                    cellDim.height * (y + 1) + if (tileType == STITCH_TYPE_PEYOTE && x % 2 == 0 && x != 0) cellDim.height / 2 else 0.0f,
                ),
                Paint().apply {
                    color = if (y % 10 == 5) Color.DarkGray else Color.Black
                    strokeWidth = lineWidth
                    alpha = a
                }
            )
        }
    }

    fun drawHeaderCell(x: Int, y: Int, label: Int) {
        canvas.drawRect(
            cellDim.width * x,
            cellDim.height * y,
            cellDim.width * (x + 1),
            cellDim.height * (y + 1),
            Paint().apply {
                color = Color.LightGray
                style = PaintingStyle.Stroke
                strokeWidth = 3.0f
            }
        )
        val skiaCanvas: org.jetbrains.skia.Canvas = canvas.nativeCanvas
        val line = PrintGraphicsUtils.headerLine(label)
        val paint = org.jetbrains.skia.Paint().apply {
            color = org.jetbrains.skia.Color.BLACK
            isAntiAlias = true
        }
        val textWidth = headerFont.measureTextWidth(label.toString(), paint)
        skiaCanvas.drawTextLine(
            line,
            cellDim.width * (x.toFloat() + 0.5f) - textWidth / 2,
            cellDim.height * (y.toFloat() + 1f) + (headerFont.metrics.ascent - headerFont.metrics.descent) / 2,
            paint
        )
    }

    fun drawPageCell() {
        canvas.drawRect(
            0f,
            0f,
            cellDim.width,
            cellDim.height,
            Paint().apply {
                color = Color.LightGray
                style = PaintingStyle.Stroke
                strokeWidth = 3.0f
            }
        )
        val skiaCanvas: org.jetbrains.skia.Canvas = canvas.nativeCanvas
        val line = TextLine.make(pageIndex, headerFont)
        val paint = org.jetbrains.skia.Paint().apply {
            color = org.jetbrains.skia.Color.BLACK
            isAntiAlias = true
        }
        val textWidth = headerFont.measureTextWidth(pageIndex, paint)
        skiaCanvas.drawTextLine(
            line,
            cellDim.width / 2 - textWidth / 2,
            cellDim.height + (headerFont.metrics.ascent - headerFont.metrics.descent) / 2,
            paint
        )
    }

    if (withLabels) {
        drawPageCell()
        (1..gridHeight).forEach { iy ->
            if (cells.containsKey(1 + dW to iy + dH))
                drawHeaderCell(0, iy, iy + dH)
        }
    }
    (1..gridWidth).forEach { ix ->
        if (withLabels) {
            if (cells.containsKey(ix + dW to 1 + dH))
                drawHeaderCell(ix, 0, ix + dW)
        }
        (1..gridHeight).forEach { iy ->
            drawCell(ix, iy, cells.getOrDefault(ix + dW to iy + dH, defCell))
        }
    }
    (0..gridWidth).forEach { ix ->
        (0..gridHeight).forEach { iy ->
            if (cells.containsKey((if (ix > 0) ix else iy) + dW to (if (iy > 0) iy else 1) + dH))
                drawGrid(ix, iy, if (ix == 0 || iy == 0 || cells.containsKey(ix + dW to iy + dH)) 1.0f else 0.0f)
        }
    }

    return bitmap
}

fun mostContrastingBW(color: Color): Int {
    val l = color.luminance()
    val contrastWithBlack = (l + 0.05) / (0.05)
    val contrastWithWhite = (1.05) / (l + 0.05)
    return if (contrastWithBlack > contrastWithWhite) org.jetbrains.skia.Color.BLACK else org.jetbrains.skia.Color.WHITE
}