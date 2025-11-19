package com.example.absolutecinema.ui.screens

import com.example.absolutecinema.data.Results
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.absolutecinema.data.getNowPlayingMovies
import com.example.absolutecinema.data.getPopularMovies
import com.example.absolutecinema.data.getTopRatedMovies
import com.example.absolutecinema.data.getUpcomingMovies
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.theme.darkBlue

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TopicScreen(
    onMovieClick:(Deliverables)->Unit,
    index:Int,
    goBack:()->Unit,
)
{
    var movies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var topicName by remember { mutableStateOf("") }
    LaunchedEffect(index) {
        isLoading = true
            when (index) {
                1 -> getPopularMovies { result ->
                    movies = result
                    isLoading = false
                    topicName="Popular"
                }
                2 -> getNowPlayingMovies { result ->
                    movies = result
                    isLoading = false
                    topicName="Now Playing"
                }
                3 -> getUpcomingMovies { result ->
                    movies = result
                    isLoading = false
                    topicName="Upcoming"
                }
                4 -> getTopRatedMovies { result ->
                    movies = result
                    isLoading = false
                    topicName="Top Rated"
                }
            }
        }


    Column(modifier = Modifier.fillMaxSize().background(darkBlue)) {



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

            Column {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back",
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                        .size(28.dp)
                        .clickable {
                        goBack()
                    }
                )
                Text(
                    topicName,
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
}