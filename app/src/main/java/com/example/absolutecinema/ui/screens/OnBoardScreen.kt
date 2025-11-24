package com.example.absolutecinema.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absolutecinema.R
import kotlinx.coroutines.launch

@Composable
@Preview(showBackground = true)
fun OnBoarding() {

    val pages = OnBoardingItem.get()
    val pagerState = rememberPagerState(initialPage = 0,
        pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        TopSection()

        HorizontalPager(
            pageCount = pages.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.75f)
        ) { page ->
            OnBoardingItemView(pages[page])
        }

        BottomSection(
            size = pages.size,
            index = pagerState.currentPage,
            onNextClicked = {
                scope.launch {
                    if (pagerState.currentPage < pages.lastIndex) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        // TODO: navigate to main screen here
                    }
                }
            }
        )
    }
}

@Composable
fun TopSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Outlined.KeyboardArrowLeft, contentDescription = null, tint = Color.White)
        }

        TextButton(
            onClick = { /* navigate to home */ },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(text = "Skip", color = Color.White)
        }
    }
}

@Composable
fun BottomSection(
    size: Int,
    index: Int,
    onNextClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Indicators(size = size, index = index)

        FloatingActionButton(
            onClick = onNextClicked,
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(
                text = if (index == size - 1) "Start" else "Next",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BoxScope.Indicators(size: Int, index: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.align(Alignment.CenterStart)
    ) {
        repeat(size) {
            Indicator(isSelected = it == index)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {

    val width = animateDpAsState(
        targetValue = if (isSelected) 22.dp else 10.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) Color.White else Color.White.copy(alpha = 0.3f)
            )
    )
}

@Composable
fun OnBoardingItemView(item: OnBoardingItem) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Image(
            painter = painterResource(id = item.image),
            contentDescription = item.title,
            modifier = Modifier.size(280.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = item.title,
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = item.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

data class OnBoardingItem(
    val image: Int,
    val title: String,
    val description: String,
    val pageNumber: Int
) {
    companion object {
        fun get() = listOf(
            OnBoardingItem(
                title = "Your Entertainment, Anywhere",
                description = "Discover movies, shows, and more all tailored to your taste and right at your fingertips.",
                image = R.drawable.cinema,
                pageNumber = 0
            ),
            OnBoardingItem(
                title = "Find What You Love, Effortlessly",
                description = "Instant access to your favorite movies with smooth navigation and zero hassle.",
                image = R.drawable.watchtv,
                pageNumber = 1
            )
        )
    }
}
