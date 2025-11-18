package org.oakim.img2scheme.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.oakim.img2scheme.AppState

@Composable
fun IconMenuBtn(icon: ImageVector,
                description: String,
                handler: (AppState) -> Unit,
                appState: AppState,
                enabled: Boolean = true) =
    Button(
        onClick = { handler(appState) },
        modifier = Modifier.size(50.dp).padding(0.dp),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier.padding(5.dp).scale(1f)
        )
    }