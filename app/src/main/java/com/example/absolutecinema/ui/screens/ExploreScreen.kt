package com.example.absolutecinema.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.data.api.Results
import com.example.absolutecinema.data.api.getMoviesOnDate
import com.example.absolutecinema.data.api.getNowPlayingMovies
import com.example.absolutecinema.data.api.getPopularMovies
import com.example.absolutecinema.data.api.getTopRatedMovies
import com.example.absolutecinema.data.api.getUpcomingMovies
import com.example.absolutecinema.data.helpers.randomNumber
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.componants.BannerAd
import com.example.absolutecinema.ui.theme.SlideInFromLeft
import com.example.absolutecinema.ui.theme.darkBlue
import com.example.absolutecinema.viewmodel.FirebaseViewModel
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun ExploreScreen(
    onMovieClick: (Deliverables) -> Unit,
    goToMovies: (Int) -> Unit,
    firebaseViewModel: FirebaseViewModel,
    time: (String) -> Unit,
) {

    var topRatedMovies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var upcomingMovies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var popularMovies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var nowPlayingMovies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var seasonMovies by remember { mutableStateOf<List<Results>>(emptyList()) }


    val firstName by firebaseViewModel.firstName.observeAsState("")

    LaunchedEffect(Unit) {

        firebaseViewModel.fetchUserFirstName()

        getPopularMovies { popularMovies = it }
        getNowPlayingMovies { nowPlayingMovies = it }
        getUpcomingMovies { upcomingMovies = it }
        getTopRatedMovies { topRatedMovies = it }
        getMoviesOnDate(randomNumber()) { seasonMovies = it }

    }


    val isLoading = popularMovies.isEmpty() ||
            nowPlayingMovies.isEmpty() ||
            upcomingMovies.isEmpty() ||
            topRatedMovies.isEmpty() ||
            seasonMovies.isEmpty()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBlue),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()

            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBlue)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ✅ Display firstName from observed LiveData
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text(
                    text = if (firstName.isNotEmpty()) "Welcome, $firstName" else "Welcome",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

            }
            BannerAd()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ){
                    Text(
                        "Today's Must Watch",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                    TextButton(
                        onClick = { goToMovies(5) },
                    ) {
                        Text("See All", color = Color(0xFF00BCD4), fontWeight = FontWeight.SemiBold)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "See All",
                            modifier = Modifier.size(18.dp),
                            Color(0xFF00BCD4)
                        )
                    }
                }
                EndlessHorizontalPager(seasonMovies, onMovieClick)
            }

            SlideInFromLeft {
                TopicList(
                    "Popular",
                    movies = popularMovies,
                    onMovieClick = onMovieClick,
                    index = 1,
                    goToMovies = goToMovies
                )
            }
            SlideInFromLeft {
                TopicList(
                    "Now playing",
                    movies = nowPlayingMovies,
                    onMovieClick = onMovieClick,
                    index = 2,
                    goToMovies = goToMovies
                )
            }
            SlideInFromLeft {
                TopicList(
                    "Upcoming",
                    movies = upcomingMovies,
                    onMovieClick = onMovieClick,
                    index = 3,
                    goToMovies = goToMovies
                )
            }

            SlideInFromLeft {
                TopicList(
                    "Top Rated",
                    movies = topRatedMovies,
                    onMovieClick = onMovieClick,
                    index = 4,
                    goToMovies = goToMovies
                )
            }

        }
    }

}

@Composable
fun TopicList(
    topicName: String,
    movies: List<Results>,
    onMovieClick: (Deliverables) -> Unit,
    index: Int,
    goToMovies: (Int) -> Unit,
) {

    Column {
        Row(
//            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                topicName,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            if (movies.size > 5) {
                TextButton(
                    onClick = { goToMovies(index) },
                ) {
                    Text("See All", color = Color(0xFF00BCD4), fontWeight = FontWeight.SemiBold)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "See All",
                        modifier = Modifier.size(18.dp),
                        Color(0xFF00BCD4)
                    )
                }
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie, onMovieClick)
            }
        }
    }
}


fun Modifier.fadingEdges(): Modifier =
    this.drawWithContent {
        drawContent()

        val fadeWidth = 60f

        // Left fade
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startX = 0f,
                endX = fadeWidth
            ),
            size = size
        )

        // Right fade
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color.Black),
                startX = size.width - fadeWidth,
                endX = size.width
            ),
            size = size
        )
    }

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun EndlessHorizontalPager(
    movies: List<Results>,
    onMovieClick: (Deliverables) -> Unit,
) {
    val infiniteCount = Int.MAX_VALUE
    val startIndex = (infiniteCount / 2) - 3
    val pagerState = rememberPagerState(initialPage = startIndex) { infiniteCount }

    // auto-scroll (increment page by 1 each tick)
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            pagerState.animateScrollToPage(pagerState.currentPage + 1, animationSpec = tween(600))
        }
    }

    // The width of each page item (image container)
    val pageWidth = 180.dp      // tweak this if you want wider/narrower posters
    val imageWidth = 140.dp
    val imageHeight = 220.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.6f)
                    )
                )
            )
    ) {
        // maxWidth is the full pager container width
        val horizontalPadding = (maxWidth - pageWidth) / 2

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .fadingEdges(), // your custom fading effect
            pageSpacing = 24.dp,
            // center pages by setting symmetric padding equal to (container - pageWidth)/2
            contentPadding = PaddingValues(horizontal = horizontalPadding),
        ) { page ->

            val movie = movies[page % movies.size]

            val pageOffset = ((pagerState.currentPage - page) +
                    pagerState.currentPageOffsetFraction).absoluteValue
            val scale = lerp(start = 0.85f, stop = 1.25f, fraction = 1f - pageOffset)

            // Make the page exactly `pageWidth` wide so it's centered by the contentPadding above
            Box(
                modifier = Modifier
                    .width(pageWidth)
                    .fillMaxHeight(),     // centers vertically inside pager height
                contentAlignment = Alignment.Center
            ) {
                GlideImage(
                    model = "https://image.tmdb.org/t/p/w500/${movie.poster}",
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(0.6f, 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                        }
                        .shadow(
                            elevation = if (pageOffset < 0.1f) 20.dp else 0.dp,       // soft glow for center
                            shape = RoundedCornerShape(16.dp),
                            clip = false
                        )
                        .width(imageWidth)
                        .height(imageHeight)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onMovieClick(
                                Deliverables(
                                    movieId = movie.id,
                                    poster = movie.poster,
                                    title = movie.title
                                )
                            )
                        }
                )
            }
        }
    }
}
@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Recommended for you",
                color = Color(0xFFB0BEC5),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        TextButton(
            onClick = onSeeAllClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "See All",
                    color = Color(0xFF00E5FF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "See All",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(18.dp)
                )
            }
        }
    }
}
