package com.example.absolutecinema.ui

import Results
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absolutecinema.data.api.getNowPlayingMovies
import com.example.absolutecinema.data.api.getPopularMovies
import com.example.absolutecinema.data.api.getTopRatedMovies
import com.example.absolutecinema.data.api.getUpcomingMovies
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.viewmodel.FirebaseViewModel

@Composable
fun ExploreScreen(
    onMovieClick: (Deliverables) -> Unit,
    goToMovies: (Int) -> Unit,
    firebaseViewModel: FirebaseViewModel,
) {

    var topRatedMovies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var upcomingMovies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var popularMovies by remember { mutableStateOf<List<Results>>(emptyList()) }
    var nowPlayingMovies by remember { mutableStateOf<List<Results>>(emptyList()) }

    val firstName by firebaseViewModel.firstName.observeAsState("")

    LaunchedEffect(Unit) {

        firebaseViewModel.fetchUserFirstName()

        getPopularMovies { popularMovies = it }
        getNowPlayingMovies { nowPlayingMovies = it }
        getUpcomingMovies { upcomingMovies = it }
        getTopRatedMovies { topRatedMovies = it }
    }


    val isLoading = popularMovies.isEmpty() ||
            nowPlayingMovies.isEmpty() ||
            upcomingMovies.isEmpty() ||
            topRatedMovies.isEmpty()

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ✅ Display firstName from observed LiveData
            Text(
                text = if (firstName.isNotEmpty()) "Welcome, $firstName" else "Welcome",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TopicList(
                "Popular",
                movies = popularMovies,
                onMovieClick = onMovieClick,
                index = 1,
                goToMovies = goToMovies
            )
            TopicList(
                "Now playing",
                movies = nowPlayingMovies,
                onMovieClick = onMovieClick,
                index = 2,
                goToMovies = goToMovies
            )
            TopicList(
                "Upcoming",
                movies = upcomingMovies,
                onMovieClick = onMovieClick,
                index = 3,
                goToMovies = goToMovies
            )
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                topicName,
                modifier = Modifier.padding(8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            if (movies.size > 5) {
                TextButton(onClick = { goToMovies(index) }) {
                    Text("See All", fontWeight = FontWeight.SemiBold)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "See All",
                        modifier = Modifier.size(18.dp)
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