package org.oakim.img2scheme.image

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.fasterxml.jackson.annotation.JsonProperty

data class Cell(
    val offset: Offset,
    val size: Size,
    var color: Color,
    @JsonProperty("palette_color") var paletteColor: Color,
)