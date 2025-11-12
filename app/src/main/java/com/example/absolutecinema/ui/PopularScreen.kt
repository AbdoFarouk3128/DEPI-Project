package com.example.absolutecinema.ui

import Results
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.data.api.getMovies
import com.example.absolutecinema.data.api.searchForMovie
import com.example.absolutecinema.navigation.Deliverables

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieScreen(
    onMovieClick: (Deliverables) -> Unit,
) {
    var movies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getMovies { result ->
            movies = result
            isLoading = false
        }
    }

    // search works if you type anything
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            isLoading = true
            searchForMovie(searchQuery) { result ->
                movies = result
                isLoading = false
            }
        } else {
            // Reload movies if search is empty
            isLoading = true
            getMovies { result ->
                movies = result
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search movies") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                items(movies) { movie ->
                    GlideImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.poster}",
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
                                    isWatched = movie.isWatched,
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
