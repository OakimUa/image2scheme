package org.oakim.img2scheme.tiles

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.oakim.img2scheme.AppState
import org.oakim.img2scheme.Constants.BRIGHTNESS_MAX
import org.oakim.img2scheme.Constants.BRIGHTNESS_MIN
import org.oakim.img2scheme.Constants.BRIGHTNESS_NORMAL
import org.oakim.img2scheme.Constants.CONTRAST_MAX
import org.oakim.img2scheme.Constants.CONTRAST_MIN
import org.oakim.img2scheme.Constants.CONTRAST_NORMAL
import org.oakim.img2scheme.Constants.SATURATION_MAX
import org.oakim.img2scheme.Constants.SATURATION_MIN
import org.oakim.img2scheme.Constants.SATURATION_NORMAL

@Composable
fun ImageTuneTile(appState: AppState) =
    with(appState) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp)),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Brightness:", modifier = Modifier.width(110.dp))
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    valueRange = BRIGHTNESS_MIN..BRIGHTNESS_MAX,
                    modifier = Modifier.width(130.dp)
                )
                Text("${(brightness * 100).toInt()}%", modifier = Modifier.width(60.dp))
                Button(
                    onClick = { brightness = BRIGHTNESS_NORMAL },
                    modifier = Modifier.width(30.dp).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("x") }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Saturation:", modifier = Modifier.width(110.dp))
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = SATURATION_MIN..SATURATION_MAX,
                    modifier = Modifier.width(130.dp)
                )
                Text("${(saturation * 100).toInt()}%", modifier = Modifier.width(60.dp))
                Button(
                    onClick = { saturation = SATURATION_NORMAL },
                    modifier = Modifier.width(30.dp).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("x") }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Contrast (γ):", modifier = Modifier.width(110.dp))
                Slider(
                    value = contrast,
                    onValueChange = { contrast = it },
                    valueRange = CONTRAST_MIN..CONTRAST_MAX,
                    modifier = Modifier.width(130.dp)
                )
                Text("${(contrast * 100).toInt()}%", modifier = Modifier.width(60.dp))
                Button(
                    onClick = { contrast = CONTRAST_NORMAL },
                    modifier = Modifier.width(30.dp).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("x") }
            }
        }
    }