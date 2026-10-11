package com.morningstar.pulsarpop.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@Composable
fun CosmicStarfield(pulseFactor: Float = 1.0f) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val random = Random(42)
        for (_idx in 0..45) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = (1.5f + random.nextFloat() * 2.5f) * pulseFactor
            val alpha = 0.15f + random.nextFloat() * 0.45f

            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CosmicStarfieldPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0B1E))
    ) {
        CosmicStarfield()
    }
}
