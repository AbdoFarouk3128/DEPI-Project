package com.example.absolutecinema.ui

import Results
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.R
import com.example.absolutecinema.data.api.getDetails
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.theme.darkBlue
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistMoviesViewModel,
    onMovieClick: (Deliverables) -> Unit
) {
    val watchlist by viewModel.watchlist.observeAsState(emptyList())
    var movies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(watchlist) {
        if (watchlist.isNotEmpty()) {
            val watchlistMovies = mutableListOf<Results>()
            for (movie in watchlist) {
                getDetails(movie.movieId) { result ->
                    result?.let { watchlistMovies.add(it) }
                    movies = watchlistMovies.toList()
                }
            }
        }
        isLoading = false
    }

    // FULL BACKGROUND
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBlue)
    ) {

        if (watchlist.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(darkBlue),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.no_watchlist),
                    contentDescription = "No movies in watchlist",
                    modifier = Modifier.size(150.dp)
                )
                Text("No movies added", color = Color.White)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .background(darkBlue)
            ) {
                items(movies) { movie ->
                    GlideImage(
                        model = "https://image.tmdb.org/t/p/w500/${movie.poster}",
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val deliverable = Deliverables(
                                    movieId = movie.id,
                                    poster = movie.poster,
                                    title = movie.title
                                )
                                onMovieClick(deliverable)
                            }
                    )
                }
            }
        }
    }
}
