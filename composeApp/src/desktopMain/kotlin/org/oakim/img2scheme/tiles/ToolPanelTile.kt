package org.oakim.img2scheme.tiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.oakim.img2scheme.AppState

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ToolPanelTile(appState: AppState) =
    Column(
        modifier = Modifier.width(350.dp).padding(start = 5.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        ImageTuneTile(appState)
        GridConfigTile(appState)
        PaletteTile(appState)
    }