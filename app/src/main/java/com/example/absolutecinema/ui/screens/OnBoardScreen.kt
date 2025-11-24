package com.example.absolutecinema.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.absolutecinema.R

data class OnBoardModel(
    val imageRes: Int,
    val title: String,
    val description: String,
    val pageNumber:Int
)

private val pages = listOf(
    OnBoardModel(
        title = "Your Entertainment, Anywhere",
        description = "Discover movies, shows, and more all tailored to your taste and right at your fingertips.",
        imageRes = R.drawable.cinema,
        pageNumber = 0
    ),
    OnBoardModel(
        title = "Find What You Love, Effortlessly",
        description = "Instant access to your favorite movies with smooth navigation and zero hassle.",
        imageRes = R.drawable.watchtv,
        pageNumber = 1,
    )
)

@Composable
fun OnBoardScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = 0) { pages.size }
        val buttonState = remember {
            derivedStateOf {
                when (pagerState.currentPage) {
                    0 -> listOf("", "Next")
                    2 -> listOf("Back", "Get Started")
                    else -> listOf("", "")
                }
            }
        }

    }
}


