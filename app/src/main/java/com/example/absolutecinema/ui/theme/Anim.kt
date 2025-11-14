package com.example.absolutecinema.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Animation(
    startAnimation: Boolean,
    initialOffsetY: Dp = 50.dp,
    duration: Int = 1000,
    content: @Composable (Modifier) -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = duration)
    )
    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else initialOffsetY,
        animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )

    content(
        Modifier
            .offset(y = offsetY)
            .alpha(alpha)
    )
}