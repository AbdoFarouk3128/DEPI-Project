package com.example.absolutecinema.ui


import Results
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserListsScreen(
    likedMoviesViewModel: LikedMoviesViewModel,
    watchlistMoviesViewModel: WatchlistMoviesViewModel,
    watchedMoviesViewModel: WatchedMoviesViewModel,
    ratedMovieViewModel: RatedMovieViewModel,
    listType: String,
    goBack: () -> Unit,
    onMovieClick: (Deliverables) -> Unit
) {
    val likedList by likedMoviesViewModel.likedList.observeAsState(emptyList())
    val watchlistList by watchlistMoviesViewModel.watchlist.observeAsState(emptyList())
    val watchedList by watchedMoviesViewModel.watchedList.observeAsState(emptyList())
    val ratedList by ratedMovieViewModel.ratedMovies.observeAsState(emptyList())
    var movies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(listType) {
        when (listType) {
            "watchlist" -> if (watchlistList.isNotEmpty()) {
                val watchlistMovies = mutableListOf<Results>()
                for (movie in watchlistList) {
                    getDetails(movie.movieId) { result ->
                        result?.let { watchlistMovies.add(it) }
                        movies = watchlistMovies.toList()
                    }
                }
            }

            "liked" -> if (likedList.isNotEmpty()) {
                val likedMovies = mutableListOf<Results>()
                for (movie in likedList) {
                    getDetails(movie.movieId) { result ->
                        result?.let { likedMovies.add(it) }
                        movies = likedMovies.toList()
                    }
                }
            }

            "watched" -> if (watchedList.isNotEmpty()) {
                val watchedMovies = mutableListOf<Results>()
                for (movie in watchedList) {
                    getDetails(movie.movieId) { result ->
                        result?.let { watchedMovies.add(it) }
                        movies = watchedMovies.toList()
                    }
                }
            }

            "rated" -> if (ratedList.isNotEmpty()) {
                val ratedMovies = mutableListOf<Results>()
                for (movie in ratedList) {
                    getDetails(movie.movieId) { result ->
                        result?.let { ratedMovies.add(it) }
                        movies = ratedMovies.toList()
                    }
                }
            }
        }
        if (likedList.isNotEmpty()) {
            val likedMovies = mutableListOf<Results>()
            for (movie in likedList) {
                getDetails(movie.movieId) { result ->
                    result?.let { likedMovies.add(it) }
                    movies = likedMovies.toList()
                }
            }
        }
        isLoading = false
    }

    // FULL SCREEN BACKGROUND
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBlue)
    ) {


        Column {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go Back",
                tint = Color.White,
                modifier = Modifier
                    .padding(12.dp)
                    .size(28.dp)
                    .clickable {
                        goBack()
                    }
            )
            Text(
                listType,
                modifier = Modifier.padding(8.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

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

