package org.oakim.img2scheme

import androidx.compose.ui.graphics.Color
import org.oakim.img2scheme.image.Cell

object Constants {
    const val SCHEME_FILE_EXTENSION = ".jsonscheme"
    val SUPPORTED_IMAGE_EXTENSIONS = setOf(".jpeg", ".jpg", ".png", ".gif", ".webp", SCHEME_FILE_EXTENSION)
    const val GRID_FREE = "generated"
    const val GRID_FIXED = "modified"
    const val STITCH_TYPE_SQUARE = "Square"
    const val STITCH_TYPE_BRICK = "Brick"
    const val STITCH_TYPE_PEYOTE = "Peyote"
    const val BRIGHTNESS_NORMAL = 0f
    const val BRIGHTNESS_MIN = -1f
    const val BRIGHTNESS_MAX = 1f
    const val SATURATION_NORMAL = 1f
    const val SATURATION_MIN = 0f
    const val SATURATION_MAX = 3f
    const val CONTRAST_NORMAL = 1f
    const val CONTRAST_MIN = 0f
    const val CONTRAST_MAX = 3f
    const val BEAD_RATIO_BASE = 2
    const val BEAD_RATIO_SQUARE = 2
    const val BEAD_RATIO_OVAL = 3
    val COLOR_DEFAULT = Color.White

    val stitchTypes = mapOf(
        STITCH_TYPE_SQUARE to Unit,
        STITCH_TYPE_PEYOTE to Unit,
        STITCH_TYPE_BRICK to Unit,
    )

    val backingGrid: MutableMap<Pair<Int, Int>, Cell> = mutableMapOf()
}