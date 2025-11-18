package org.oakim.img2scheme.serialization

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.oakim.img2scheme.AppState
import org.oakim.img2scheme.image.Cell

data class Scheme @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @JsonProperty("image_bitmap") val imageBitmap: ImageBitmap,
    @JsonProperty("image_file_name") val imageFileName: String,
    @JsonProperty("brightness") val brightness: Float,
    @JsonProperty("saturation") val saturation: Float,
    @JsonProperty("contrast") val contrast: Float,
    @JsonProperty("scale") val scale: Float,
    @JsonProperty("offset") val offset: Offset,
    @JsonProperty("palette") val palette: Map<Int, Color>,
    @JsonProperty("selected_bead_ratio") val selectedBeadRatio: Int,
    @JsonProperty("grid_width") val gridWidth: Int,
    @JsonProperty("grid_height") val gridHeight: Int,
    @JsonProperty("grid_state") val gridState: String,
    @JsonProperty("stitch_type") val stitchType: String,
    @JsonProperty("grid") val grid: Map<Pair<Int, Int>, Cell>,
) {
    companion object {
        fun fromAppState(state: AppState): Scheme =
            with(state) {
                Scheme(
                    imageBitmap = imageBitmap!!,
                    imageFileName = imageFileName!!,
                    brightness = brightness,
                    saturation = saturation,
                    contrast = contrast,
                    scale = scale,
                    offset = offset,
                    palette = displayablePalette,
                    selectedBeadRatio = selectedBeadRatio,
                    gridWidth = gridWidth,
                    gridHeight = gridHeight,
                    grid = grid,
                    gridState = gridState,
                    stitchType = selectedStitchType
                )
            }
    }
}
