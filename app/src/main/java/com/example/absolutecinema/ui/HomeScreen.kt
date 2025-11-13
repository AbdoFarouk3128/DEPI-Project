package com.example.absolutecinema.ui

import Results
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.data.api.Genre
import com.example.absolutecinema.data.api.discoverMoviesByGenre
import com.example.absolutecinema.data.api.getPopularMovies
import com.example.absolutecinema.data.api.searchForMovie
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.viewmodel.GenreViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(
    onMovieClick: (Deliverables) -> Unit,
    genreViewModel: GenreViewModel = viewModel()
) {
    var movies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenreId by remember { mutableStateOf<String?>(null) }
    val genres by genreViewModel.genres.collectAsState()

    LaunchedEffect(Unit) {
        isLoading = true
        getPopularMovies { result ->
            movies = result
            isLoading = false
        }
        genreViewModel.loadGenres()
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
            getPopularMovies  { result ->
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

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre: Genre ->
                Button(
                    onClick = {
                        selectedGenreId = genre.id.toString()
                        isLoading = true
                        discoverMoviesByGenre(genre.id.toString()) { result ->
                            movies = result
                            isLoading = false
                        }
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text(genre.name)
                }
            }
        }

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
