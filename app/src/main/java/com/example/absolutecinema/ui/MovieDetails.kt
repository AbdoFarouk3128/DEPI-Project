package com.example.absolutecinema.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.example.absolutecinema.R
import com.example.absolutecinema.data.*
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.componants.RatingBar
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel

@Composable
fun MovieDetails(
    movieId: String,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedListViewModel: LikedMoviesViewModel,
    onBack: () -> Unit,
    onMovieClick: (Deliverables) -> Unit,
    watchlistControl: (String, String) -> Unit,
    likedListControl: (String, String) -> Unit,
    gotoWatchlist: () -> Unit,
    gotoLikedList: () -> Unit
) {
    var movieDetails by remember { mutableStateOf<MovieDetails?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Set system bar colors
    val view = LocalView.current
    SideEffect {
        val window: Window? = (view.context as? Activity)?.window
        window?.statusBarColor = Color.Black.toArgb()
        window?.navigationBarColor = Color.Black.toArgb()
    }

    LaunchedEffect(movieId) {
        getMovieDetails(movieId) {
            movieDetails = it
        }
    }

    if (showBottomSheet) {
        ManageMovieSheet(
            movieDetails = movieDetails,
            watchlistViewModel = watchlistViewModel,
            likedListViewModel = likedListViewModel,
            watchlistControl = watchlistControl,
            likedListControl = likedListControl,
            gotoWatchlist = gotoWatchlist,
            gotoLikedList = gotoLikedList,
            onDismiss = { showBottomSheet = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        if (movieDetails != null) {
            val details = movieDetails!!
            item { BackdropHeader(details, onBack) }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = details.overview,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            details.credits?.cast?.let { cast ->
                if (cast.isNotEmpty()) {
                    item {
                        CastSection(cast)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            if (details.recommendations.isNotEmpty()) {
                item {
                    RecommendationsSection(details.recommendations, onMovieClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Button(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Manage Movie")
                }
            }
        } else {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BackdropHeader(movie: MovieDetails, onBack: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        GlideImage(
            model = "https://image.tmdb.org/t/p/w1280/${movie.backdropPath}",
            contentDescription = movie.originalTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            GlideImage(
                model = "https://image.tmdb.org/t/p/w500/${movie.posterPath}",
                contentDescription = movie.originalTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = movie.originalTitle,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${movie.releaseDate.substringBefore("-")} \u2022 ${movie.genres.joinToString { it.name }}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                movie.imdbId?.let { imdbId ->
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$imdbId/"))
                        context.startActivity(intent)
                    }) {
                        Text("View on IMDb", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CastSection(cast: List<CastMember>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Cast", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cast) { member ->
                CastMemberItem(member)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CastMemberItem(member: CastMember) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        // First, check if the profilePath from the API is null or empty.
        if (member.profilePath.isNullOrEmpty()) {
            // If the path is missing, display your local vector drawable directly.
            // This avoids calling Glide with a bad URL.
            Icon(
                painter = painterResource(id = R.drawable.ic_person_placeholder),
                contentDescription = member.name, // Use the name for accessibility
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        } else {
            // If the path exists, use GlideImage to load the image from the network.
            // We remove all failure/error parameters to avoid compilation errors.
            GlideImage(
                model = "https://image.tmdb.org/t/p/w200/${member.profilePath}",
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        }
        Text(
            text = member.name,
            fontSize = 12.sp,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RecommendationsSection(recommendations: List<MoviesRelated>, onMovieClick: (Deliverables) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("You might also like", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recommendations) { movie ->
                RecommendedMovieItem(movie, onMovieClick)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RecommendedMovieItem(movie: MoviesRelated, onMovieClick: (Deliverables) -> Unit) {
    GlideImage(
        model = "https://image.tmdb.org/t/p/w500/${movie.poster}",
        contentDescription = movie.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onMovieClick(Deliverables(movieId = movie.id, poster = movie.poster ?: "", title = movie.title))
            }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMovieSheet(
    movieDetails: MovieDetails?,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedListViewModel: LikedMoviesViewModel,
    watchlistControl: (String, String) -> Unit,
    likedListControl: (String, String) -> Unit,
    gotoWatchlist: () -> Unit,
    gotoLikedList: () -> Unit,
    onDismiss: () -> Unit
) {
    val watchlist by watchlistViewModel.watchlist.observeAsState(emptyList())
    val likedList by likedListViewModel.likedList.observeAsState(emptyList())

    // States for the icons derived from ViewModels
    val isInWatchlist = movieDetails?.id?.let { id -> watchlist.any { it.movieId == id } } ?: false
    val isLiked = movieDetails?.id?.let { id -> likedList.any { it.movieId == id } } ?: false

    // Local state for the "watched" icon
    var isActuallyWatched by remember { mutableStateOf(false) }
    var rating1 by remember { mutableIntStateOf(0) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon 1: Watchlist
                Icon(
                    painter = painterResource(if (isInWatchlist) R.drawable.watchlist_added else R.drawable.watchlist_not),
                    contentDescription = "Watchlist Icon",
                    modifier = Modifier
                        .clickable {
                            movieDetails?.let { watchlistControl(it.id, it.posterPath ?: "") }
                        }
                        .size(120.dp)
                        .padding(8.dp)
                )
                // Icon 2: Like
                Icon(
                    painter = painterResource(if (isLiked) R.drawable.heart else R.drawable.heart_empty),
                    contentDescription = "Liked Icon",
                    modifier = Modifier
                        .clickable {
                            movieDetails?.let { likedListControl(it.id, it.posterPath ?: "") }
                        }
                        .size(120.dp)
                        .padding(8.dp)
                )
                // Icon 3: Watched
                Icon(
                    painter = painterResource(if (isActuallyWatched) R.drawable.watched else R.drawable.not_watched),
                    contentDescription = "Watched Icon",
                    modifier = Modifier
                        .clickable {
                            isActuallyWatched = !isActuallyWatched
                            // TODO: Add your logic here to call a ViewModel for the watched status
                        }
                        .size(120.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fixed RatingBar with proper parameters
            movieDetails?.let { movie ->
                RatingBar(
                    modifier = Modifier.height(50.dp),
                    rating = rating1,
                    onRatingChanged = {
                        rating1 = it
                    },
                    stars = 5,
                    starsColor = Color.Yellow,
                    movieId = movie.id,  // ✅ Fixed: Pass the actual movie ID
                    saveRating = { movieId, rating ->  // ✅ Fixed: Lambda to save rating
                        // Add your logic to save the rating to database/ViewModel
                        // For example: ratingViewModel.saveRating(movieId, rating)
                    }
                )
            }
        }
    }
}