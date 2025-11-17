package com.example.absolutecinema.ui.screens

import Results
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.data.getDetails
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.theme.darkBlue
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel

@Composable
fun ListsScreen(
    watchlistViewModel: WatchlistMoviesViewModel,
    likedMoviesViewModel: LikedMoviesViewModel,
    watchedMoviesViewModel: WatchedMoviesViewModel,
    ratedMovieViewModel: RatedMovieViewModel,
    onMovieClick: (Deliverables) -> Unit,
    onSeeAllClick: (String) -> Unit,
) {
    // ✅ Observe data from ViewModels (auto-updates when Firebase changes)
    val watchlist by watchlistViewModel.watchlist.observeAsState(emptyList())
    val likedList by likedMoviesViewModel.likedList.observeAsState(emptyList())
    val watchedList by watchedMoviesViewModel.watchedList.observeAsState(emptyList())
    val ratedList by ratedMovieViewModel.ratedMovies.observeAsState(emptyList())

    var isLoading by remember { mutableStateOf(true) }

    // ✅ Load data from Firebase ONCE and listen for real-time updates
    LaunchedEffect(Unit) {
        // Initial load
        watchlistViewModel.loadWatchlist()
        likedMoviesViewModel.loadLikedMovies()
        watchedMoviesViewModel.loadWatchedMovies()
        ratedMovieViewModel.fetchRatedMovies()

        // Set up real-time listeners for automatic updates
        watchlistViewModel.listenToWatchlistChanges()

        // Give it 2 seconds for initial load
        kotlinx.coroutines.delay(2000)
        isLoading = false
    }

    if (isLoading) {
        // ✅ Loading state
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
                .padding(vertical = 16.dp)
        ) {
            // Header
            Text(
                text = "My Lists",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

            )

            Spacer(modifier = Modifier.height(16.dp))

            // Watchlist Section
            MovieListSection(
                title = "Watchlist",
                movieIds = watchlist.map { it.movieId },
                totalCount = watchlist.size,
                color = Color(0xFF6200EA),
                onMovieClick = onMovieClick,
                onSeeAllClick = { onSeeAllClick("watchlist") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Liked Section
            MovieListSection(
                title = "Liked",
                movieIds = likedList.map { it.movieId },
                totalCount = likedList.size,
                color = Color(0xFFE91E63),
                onMovieClick = onMovieClick,
                onSeeAllClick = { onSeeAllClick("liked") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Watched Section
            MovieListSection(
                title = "Watched",
                movieIds = watchedList.map { it.movieId },
                totalCount = watchedList.size,
                color = Color(0xFF00BCD4),
                onMovieClick = onMovieClick,
                onSeeAllClick = { onSeeAllClick("watched") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Rated Section
            MovieListSection(
                title = "Rated",
                movieIds = ratedList.map { it.movieId },
                totalCount = ratedList.size,
                color = Color(0xFFFF9800),
                onMovieClick = onMovieClick,
                onSeeAllClick = { onSeeAllClick("rated") }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MovieListSection(
    title: String,
    movieIds: List<String>,
    totalCount: Int,
    color: Color,
    onMovieClick: (Deliverables) -> Unit,
    onSeeAllClick: () -> Unit
) {
    var movies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var isLoadingMovies by remember { mutableStateOf(false) }

    // ✅ Fetch only first 5 movies - triggers when movieIds changes
    LaunchedEffect(movieIds) {
        if (movieIds.isNotEmpty()) {
            isLoadingMovies = true
            val fetchedMovies = mutableListOf<Results>()
            var completedCount = 0

            movieIds.take(5).forEach { movieId ->
                getDetails(movieId) { result ->
                    result?.let { fetchedMovies.add(it) }
                    completedCount++

                    if (completedCount == movieIds.take(5).size) {
                        movies = fetchedMovies.toList()
                        isLoadingMovies = false
                    }
                }
            }
        } else {
            movies = emptyList()
            isLoadingMovies = false
        }
    }

    Column {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$totalCount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (totalCount > 5) {
                TextButton(onClick = onSeeAllClick) {
                    Text("See All", color = color, fontWeight = FontWeight.SemiBold)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "See All",
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Movies Row
        if (isLoadingMovies) {
            // ✅ Loading state for individual sections
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        } else if (movies.isEmpty()) {
            EmptyListCard(title, color)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(movies) { movie ->
                    MovieCard(movie, onMovieClick)
                }

                // "See More" card if there are more than 5 movies
                if (totalCount > 5) {
                    item {
                        SeeMoreCard(
                            remaining = totalCount - 5,
                            color = color,
                            onClick = onSeeAllClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieCard(movie: Results, onMovieClick: (Deliverables) -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable {
                onMovieClick(
                    Deliverables(
                        movieId = movie.id,
                        poster = movie.poster,
                        title = movie.title
                    )
                )
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        GlideImage(
            model = "https://image.tmdb.org/t/p/w500/${movie.poster}",
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SeeMoreCard(remaining: Int, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "See More",
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "+$remaining",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = "more",
                fontSize = 12.sp,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EmptyListCard(listName: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No movies in $listName yet",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}