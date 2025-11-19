package com.example.absolutecinema.ui.screens

import com.example.absolutecinema.data.Results
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.data.Genre
import com.example.absolutecinema.data.discoverMoviesByGenre
import com.example.absolutecinema.data.getPopularMovies
import com.example.absolutecinema.data.searchForMovie
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.theme.darkBlue
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
    var selectedGenreIds by remember { mutableStateOf(setOf<Int>()) }

    val genres by genreViewModel.genres.collectAsState()

    // Initially load popular movies and genres
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

    Column(modifier = Modifier.fillMaxSize()
        .background(darkBlue)) {

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search movies") },
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        )


        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre: Genre ->
                val isSelected = selectedGenreIds.contains(genre.id)

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else Color.LightGray,
                    label = ""
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.Black,
                    label = ""
                )

                Button(
                    onClick = {
                        selectedGenreIds = if (isSelected) {
                            selectedGenreIds - genre.id
                        } else {
                            selectedGenreIds + genre.id
                        }

                        // Fetch movies immediately based on selected genres
                        isLoading = true
                        if (selectedGenreIds.isEmpty()) {
                            getPopularMovies { result ->
                                movies = result
                                isLoading = false
                            }
                        } else {
                            discoverMoviesByGenre(selectedGenreIds.joinToString(",")) { result ->
                                movies = result
                                isLoading = false
                            }
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = backgroundColor,
                        contentColor = textColor
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = genre.name,
                        color = textColor,
                        fontSize = 14.sp
                    )
                }
            }
        }

        //Movies Grid
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
