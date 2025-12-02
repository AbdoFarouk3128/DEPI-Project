package com.example.absolutecinema.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

@Composable
fun SlideInFromLeft(
    duration: Int = 900,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(
                durationMillis = duration,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = duration,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        content()
    }
}

@Composable
fun TVCloseAnimation(
    startAnimation: Boolean,
    duration: Int = 800,
    flashDuration: Long = 300L
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val scope = rememberCoroutineScope()
    var flashVisible by remember { mutableStateOf(false) }

    val topHeight by animateDpAsState(
        targetValue = if (startAnimation) screenHeight / 2 else 0.dp,
        animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing),
        finishedListener = {
            scope.launch {
                flashVisible = true
                delay(flashDuration)
                flashVisible = false
            }
        }
    )

    val bottomHeight by animateDpAsState(
        targetValue = if (startAnimation) screenHeight / 2 else 0.dp,
        animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topHeight)
                .background(Color.Black.copy(alpha = 0.95f))
                .align(Alignment.TopCenter)
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomHeight)
                .background(Color.Black.copy(alpha = 0.95f))
                .align(Alignment.BottomCenter)
        )


        if (flashVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.Center)
                    .background(Color.White)
            )
        }
    }
}
