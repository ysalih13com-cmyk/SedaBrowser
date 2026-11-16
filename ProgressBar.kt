package com.seda.browser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BrowserProgressBar(
    progress: Int,
    isLoading: Boolean
) {
    AnimatedVisibility(visible = isLoading) {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0, 100) / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
        )
    }
}
