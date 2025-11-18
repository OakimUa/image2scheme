package org.oakim.img2scheme.tiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.LineaIcons
import compose.icons.lineaicons.Arrows
import compose.icons.lineaicons.BasicElaboration
import compose.icons.lineaicons.Software
import compose.icons.lineaicons.arrows.CircleMinus
import compose.icons.lineaicons.arrows.CirclePlus
import compose.icons.lineaicons.arrows.Compress
import compose.icons.lineaicons.arrows.Expand
import compose.icons.lineaicons.basicelaboration.DocumentDownload
import compose.icons.lineaicons.basicelaboration.DocumentPicture
import compose.icons.lineaicons.basicelaboration.DocumentUpload
import compose.icons.lineaicons.basicelaboration.MailDownload
import org.oakim.img2scheme.AppState
import org.oakim.img2scheme.ButtonHandlers
import org.oakim.img2scheme.components.IconMenuBtn
import org.oakim.img2scheme.components.PrintPreviewDialog

@Composable
fun MenuTile(appState: AppState) =
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 10.dp, top = 5.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconMenuBtn(
            icon = LineaIcons.BasicElaboration.DocumentUpload,
            description = "Open",
            handler = ButtonHandlers::openFileBtnHandler,
            appState = appState
        )
        Spacer(modifier = Modifier.width(1.dp))
        IconMenuBtn(
            icon = LineaIcons.BasicElaboration.DocumentDownload,
            description = "Save",
            handler = ButtonHandlers::saveAs,
            appState = appState,
            enabled = appState.imageSelected
        )
        Spacer(modifier = Modifier.width(1.dp))
        IconMenuBtn(
            icon = LineaIcons.BasicElaboration.DocumentPicture,
            description = "Print",
            handler = ButtonHandlers::print,
            appState = appState,
            enabled = appState.imageSelected
        )
        Spacer(modifier = Modifier.width(10.dp))
        IconMenuBtn(
            icon = LineaIcons.Arrows.CircleMinus,
            description = "Zoom Out",
            handler = ButtonHandlers::zoomOut,
            appState = appState,
            enabled = appState.imageSelected
        )
        Spacer(modifier = Modifier.width(1.dp))
        IconMenuBtn(
            icon = LineaIcons.Arrows.Compress,
            description = "Reset",
            handler = ButtonHandlers::zoomReset,
            appState = appState,
            enabled = appState.imageSelected
        )
        Spacer(modifier = Modifier.width(1.dp))
        IconMenuBtn(
            icon = LineaIcons.Arrows.Expand,
            description = "Expand",
            handler = ButtonHandlers::zoomExpand,
            appState = appState,
            enabled = appState.imageSelected
        )
        Spacer(modifier = Modifier.width(1.dp))
        IconMenuBtn(
            icon = LineaIcons.Arrows.CirclePlus,
            description = "Zoom In",
            handler = ButtonHandlers::zoomIn,
            appState = appState,
            enabled = appState.imageSelected
        )
        Box(modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .fillMaxWidth().height(40.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                Spacer(modifier = Modifier.width(3.dp))
                Box(modifier = Modifier
                    .height(34.dp).width(50.dp)
                    .background(appState.colorUnderCursor)
                    .border(1.dp, Color.Gray, RoundedCornerShape(3.dp)))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Cursor: ${appState.imgCursor.x.toInt()} x ${appState.imgCursor.y.toInt()}", modifier = Modifier.width(200.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Mouse: ${appState.lastCursor.x.toInt()} x ${appState.lastCursor.y.toInt()}", modifier = Modifier.width(200.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Cell: ${appState.cellUnderCursor.x} x ${appState.cellUnderCursor.y}", modifier = Modifier.width(150.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("${(appState.scale * 100).toInt()}%")
                Spacer(modifier = Modifier.width(10.dp))
                Text(appState.imageFileName?.substringAfterLast("/") ?: "<file not selected>")
                Spacer(modifier = Modifier.width(10.dp))
                Text("[${appState.imageSize.width.toInt()} x ${appState.imageSize.height.toInt()}]")
            }
        }
    }