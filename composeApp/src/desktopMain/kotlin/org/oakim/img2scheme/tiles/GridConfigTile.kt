package org.oakim.img2scheme.tiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.oakim.img2scheme.AppState
import org.oakim.img2scheme.Constants
import org.oakim.img2scheme.Constants.BEAD_RATIO_OVAL
import org.oakim.img2scheme.Constants.BEAD_RATIO_SQUARE
import org.oakim.img2scheme.Constants.GRID_FREE
import org.oakim.img2scheme.Constants.STITCH_TYPE_BRICK
import org.oakim.img2scheme.components.SegmentedButtonGroup

@ExperimentalResourceApi
@Composable
fun GridConfigTile(appState: AppState) =
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
                Text(
                    "Grid",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(110.dp)
                )
                Text("OFF")
                Switch(
                    checked = showGrid,
                    onCheckedChange = { showGrid = it })
                Text("ON")
                Spacer(modifier = Modifier.width(4.dp))
                Text("[${gridState}]")
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Beads:", modifier = Modifier.width(110.dp))
                listOf(
                    "Square" to BEAD_RATIO_SQUARE,
                    "Oval" to BEAD_RATIO_OVAL
                ).forEach { (label, option) ->
                    RadioButton(
                        selected = (option == selectedBeadRatio),
                        onClick = {
                            selectedBeadRatio = option
                            calculateGridHeight()
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label, style = MaterialTheme.typography.body2)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Stitch type:", modifier = Modifier.width(110.dp))
                SegmentedButtonGroup(
                    items = Constants.stitchTypes.keys.toList(),
                    selected = selectedStitchType,
                    onSelectedChange = {
                        val calculateWidth = selectedStitchType == STITCH_TYPE_BRICK || it == STITCH_TYPE_BRICK
                        selectedStitchType = it
                        if (calculateWidth)
                            calculateGridWidth()
                        else
                            calculateGridHeight()
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cols:", modifier = Modifier.width(110.dp))
                Button(
                    onClick = {
                        if (gridWidth > 1) {
                            gridWidth--
                            gridState = GRID_FREE
                            calculateGridHeight()
                        }
                    },
                    modifier = Modifier.width(30.dp).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("<") }
                BasicTextField(
                    value = gridWidth.toString(),
                    onValueChange = { newValue ->
                        newValue.toIntOrNull()?.let {
                            gridWidth = it
                            gridState = GRID_FREE
                            calculateGridHeight()
                        }
                    },
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .height(16.dp).width(80.dp),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black)
                )
                Button(
                    onClick = {
                        gridWidth++
                        gridState = GRID_FREE
                        calculateGridHeight()
                    },
                    modifier = Modifier.width(30.dp).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text(">") }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rows:", modifier = Modifier.width(110.dp))
                Button(
                    onClick = {
                        if (gridHeight > 1) {
                            gridHeight--
                            gridState = GRID_FREE
                            calculateGridWidth()
                        }
                    },
                    modifier = Modifier.width(30.dp).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("<") }
                BasicTextField(
                    value = gridHeight.toString(),
                    onValueChange = { newValue ->
                        newValue.toIntOrNull()?.let {
                            gridHeight = it
                            gridState = GRID_FREE
                            calculateGridWidth()
                        }
                    },
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .height(16.dp).width(80.dp),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black)
                )
                Button(
                    onClick = {
                        gridHeight++
                        gridState = GRID_FREE
                        calculateGridWidth()
                    },
                    modifier = Modifier.width(30.dp).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text(">") }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
