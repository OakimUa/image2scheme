package org.oakim.img2scheme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.skia.Image

@ExperimentalResourceApi
@Composable
fun SegmentedButtonGroup(
    items: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    buttonHeight: Dp = 40.dp
) {
    Row(modifier = Modifier.padding(4.dp)) {
        items.forEachIndexed { index, item ->
            val isSelected = item == selected
            OutlinedButton(
                onClick = { onSelectedChange(item) },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.2f) else Color.Transparent
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) MaterialTheme.colors.primary else Color.Gray
                ),
                shape = when (index) {
                    0 -> MaterialTheme.shapes.small.copy(
                        topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
                    )

                    items.lastIndex -> MaterialTheme.shapes.small.copy(
                        topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                    )

                    else -> MaterialTheme.shapes.small.copy(all = CornerSize(0.dp))
                },
                modifier = Modifier
                    .height(buttonHeight)
                    .defaultMinSize(minWidth = buttonHeight)
            ) {
                Icon(
                    painter = BitmapPainter(
                        Image.makeFromEncoded(
                            (Thread.currentThread().contextClassLoader.getResourceAsStream("icons/$item.png")
                                ?: error("Resource not found: icons/$item.png")).readBytes()
                        ).toComposeImageBitmap()
                    ),
                    contentDescription = item,
                    modifier = Modifier.size(35.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}
