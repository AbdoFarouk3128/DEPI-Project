package com.example.absolutecinema.ui

import com.example.absolutecinema.viewmodel.RatedMovieViewModel


import Results
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.R
import com.example.absolutecinema.data.getDetails
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RatedScreen(
    viewModel: RatedMovieViewModel,
    onMovieClick: (Deliverables) -> Unit
)
{
    val watchedList by viewModel.ratedMovies.observeAsState(emptyList())
    var movies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(watchedList) {
        if (watchedList.isNotEmpty()) {
            val watchlistMovies = mutableListOf<Results>()

            for (movie in watchedList) {
                getDetails(movie.movieId) { result ->
                    result?.let { watchlistMovies.add(it) }
                    movies = watchlistMovies.toList()
                }
            }
            isLoading = false
        } else {
            movies = emptyList()
            isLoading = false
        }
    }



    if (watchedList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.no_rated),
                contentDescription = "No movies in Rated",
                modifier = Modifier.size(150.dp)
            )
            Text(
                "No Rated movies ",
            )

        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
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