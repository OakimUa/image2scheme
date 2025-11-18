package org.oakim.img2scheme.tiles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import compose.icons.LineaIcons
import compose.icons.lineaicons.Arrows
import compose.icons.lineaicons.Software
import compose.icons.lineaicons.arrows.Minus
import compose.icons.lineaicons.arrows.Plus
import compose.icons.lineaicons.software.Eyedropper
import compose.icons.lineaicons.software.Pencil
import org.oakim.img2scheme.AppState
import javax.swing.JColorChooser

@Composable
fun PaletteTile(appState: AppState) =
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
                Text("Palette", textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                Text("OFF")
                Switch(
                    checked = applyPalette,
                    onCheckedChange = {
                        applyPalette = it
                        if (it && !showGrid) {
                            showGrid = true
                        }
                    })
                Text("ON")
            }
            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        displayablePalette.entries.sortedBy { it.key }.forEach { (key, color) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "#$key${if (key == 0) "(T)" else ""}:",
                                    modifier = Modifier.width(70.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(80.dp, 40.dp)
                                        .background(color, RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Button(
                                    onClick = {
                                        if (colorSelectionTarget == null) {
                                            colorSelectionTarget = key
                                        }
                                    },
                                    modifier = Modifier.size(40.dp).padding(0.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    enabled = colorSelectionTarget == null && drawColorNum == null
                                ) {
                                    Icon(
                                        imageVector = LineaIcons.Software.Eyedropper,
                                        contentDescription = "Pick",
                                        modifier = Modifier.padding(5.dp).scale(1f),
                                        tint = if (colorSelectionTarget == key) Color.Black else LocalContentColor.current.copy(
                                            alpha = LocalContentAlpha.current
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.size(1.dp))
                                Button(
                                    onClick = {
                                        colorSelectionTarget = key
                                        val selectedColor = displayablePalette[key]!!
                                        val awtColor = JColorChooser.showDialog(
                                            null,
                                            "Choose color",
                                            java.awt.Color(
                                                (selectedColor.red * 255).toInt(),
                                                (selectedColor.green * 255).toInt(),
                                                (selectedColor.blue * 255).toInt()
                                            )
                                        )
                                        if (awtColor != null) {
                                            addColorToPalette(key, Color(awtColor.red, awtColor.green, awtColor.blue))
                                        } else {
                                            colorSelectionTarget = null
                                        }
                                    },
                                    modifier = Modifier.size(40.dp).padding(0.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    enabled = colorSelectionTarget == null && drawColorNum == null
                                ) {
                                    Text("...")
                                }
                                Spacer(modifier = Modifier.size(1.dp))
                                Button(
                                    onClick = {
                                        removeColorFromPalette(key)
                                    },
                                    modifier = Modifier.size(40.dp).padding(0.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    enabled = colorSelectionTarget == null && drawColorNum == null
                                ) {
                                    Icon(
                                        imageVector = LineaIcons.Arrows.Minus,
                                        contentDescription = "Remove",
                                        modifier = Modifier.padding(5.dp).scale(1f),
                                        tint = if (colorSelectionTarget == key) Color.Black else LocalContentColor.current.copy(
                                            alpha = LocalContentAlpha.current
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.size(1.dp))
                                Button(
                                    onClick = {
                                        drawColorNum = if (drawColorNum == key) null else key
                                    }, modifier = Modifier.size(40.dp).padding(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (drawColorNum == key) Color(30, 120, 30) else MaterialTheme.colors.primary,
                                        contentColor = Color.White
                                    ), contentPadding = PaddingValues(0.dp),
                                    enabled = applyPalette && colorSelectionTarget == null
                                ) {
                                    Icon(
                                        imageVector = LineaIcons.Software.Pencil,
                                        contentDescription = "Draw",
                                        modifier = Modifier.padding(5.dp).scale(1f),
                                        tint = if (colorSelectionTarget == key) Color.Black else LocalContentColor.current.copy(
                                            alpha = LocalContentAlpha.current
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(2.dp))
                        Row {
                            Button(
                                onClick = {
                                    val newKey = displayablePalette.size
                                    addColorToPalette(newKey, Color.Transparent)
                                    colorSelectionTarget = newKey
                                },
                                modifier = Modifier.width(320.dp).height(40.dp).padding(0.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.LightGray
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    Brush.linearGradient(listOf(
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue, Color.Transparent, Color.Transparent,
                                        Color.Blue))
                                )
                            ) {
                                Icon(
                                    imageVector = LineaIcons.Arrows.Plus,
                                    contentDescription = "Pick",
                                    modifier = Modifier.padding(0.dp).scale(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
            }
        }
    }