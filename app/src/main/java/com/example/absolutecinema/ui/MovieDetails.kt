package com.example.absolutecinema.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.absolutecinema.R
import com.example.absolutecinema.data.api.CastMember
import com.example.absolutecinema.data.api.MovieDetails
import com.example.absolutecinema.data.api.MoviesRelated
import com.example.absolutecinema.data.api.VideosResponse
import com.example.absolutecinema.data.api.getMovieDetails
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.componants.BottomSheet
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel

@Composable
fun MovieDetails(
    movieId: String,
    posterPath: String,
    title: String,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedListViewModel: LikedMoviesViewModel,
    watchedMoviesViewModel: WatchedMoviesViewModel,
    ratedMovieViewModel: RatedMovieViewModel,
    onBack: () -> Unit,
    onMovieClick: (Deliverables) -> Unit,
    watchlistControl: (String, String) -> Unit,
    likedListControl: (String, String) -> Unit,
    watchedListControl: (String, String) -> Unit,
    ratedListControl: (String, Int) -> Unit,
) {
    var movieDetails by remember { mutableStateOf<MovieDetails?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // ✅ Keep status bar icons DARK (visible on light background at top)
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window
        window?.let {
            // Keep status bar white/light background
            it.statusBarColor = android.graphics.Color.WHITE
            // Set icons to DARK so they're visible on white background
            androidx.core.view.WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = true
        }

        onDispose {
            // Keep the same when leaving (no change needed)
        }
    }

    LaunchedEffect(movieId) {
        getMovieDetails(movieId) {
            movieDetails = it
        }
    }

    if (showBottomSheet) {
        BottomSheet(
            movieId = movieId,
            posterPath = posterPath,
            watchlistViewModel = watchlistViewModel,
            likedListViewModel = likedListViewModel,
            watchedMoviesViewModel = watchedMoviesViewModel,
            ratedMovieViewModel = ratedMovieViewModel,
            watchlistControl = watchlistControl,
            likedListControl = likedListControl,
            watchedListControl = watchedListControl,
            ratedListControl = ratedListControl,
            onDismiss = { showBottomSheet = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (movieDetails != null) {
            val details = movieDetails!!
            item { BackdropHeader(details, onBack) }

            // About the Movie Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "About the Movie",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = details.overview,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ✅ Trailer Section (NEW!)
            if (details.videos?.results?.isNotEmpty() == true) {
                item {
                    TrailerSection(details.videos)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Modern Manage Movie Section
            item {
                ModernManageMovieSection(onClick = { showBottomSheet = true })
                Spacer(modifier = Modifier.height(24.dp))
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

@Composable
fun ModernManageMovieSection(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Manage Movie",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add to lists, rate & review",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Icon(
                painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                contentDescription = "Manage",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BackdropHeader(movie: MovieDetails, onBack: () -> Unit) {
    val context = LocalContext.current
    val voteOutOfFive = (movie.voteAverage / 2.0).toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // ✅ Back to normal height
    ) {
        // Backdrop Image
        GlideImage(
            model = "https://image.tmdb.org/t/p/w1280/${movie.backdropPath}",
            contentDescription = movie.originalTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // Back button - simple positioning
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // Movie info
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
                    .width(126.dp)
                    .height(186.dp)
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
                    fontWeight = FontWeight.Bold,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black,
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${movie.releaseDate.substringBefore("-")} • ${movie.genres.joinToString { it.name }}",
                    color = Color.White,
                    fontSize = 14.sp,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black,
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                // IMDb and Rating Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // ✅ Reduced from 8.dp to 4.dp
                ) {
                    movie.imdbId?.let { imdbId ->
                        // Rectangular IMDb Button
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5C518) // IMDb yellow
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$imdbId/"))
                                context.startActivity(intent)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.imdb_logo),
                                    contentDescription = "View on IMDb",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(45.dp) // ✅ Increased from 40.dp to 52.dp
                                )
                            }
                        }
                    }

                    // Rating Display
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = String.format("%.1f", voteOutOfFive),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "/5",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
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
        if (member.profilePath.isNullOrEmpty()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_person_placeholder),
                contentDescription = member.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        } else {
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrailerSection(videos: VideosResponse?) {
    // Get the official trailer or first trailer
    val trailer = videos?.results?.firstOrNull { it.type == "Trailer" && it.site == "YouTube" && it.official }
        ?: videos?.results?.firstOrNull { it.type == "Trailer" && it.site == "YouTube" }

    val context = LocalContext.current

    trailer?.let {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Trailer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            // YouTube Trailer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable {
                        // Open YouTube video
                        val youtubeIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=${trailer.key}")
                        )
                        context.startActivity(youtubeIntent)
                    },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // YouTube Thumbnail
                    GlideImage(
                        model = "https://img.youtube.com/vi/${trailer.key}/maxresdefault.jpg",
                        contentDescription = "Trailer Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )

                    // Play button
                    Icon(
                        painter = painterResource(R.drawable.ic_play_circle), // You'll need this icon
                        contentDescription = "Play Trailer",
                        tint = Color.White,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.Center)
                    )

                    // YouTube logo in corner
                    Image(
                        painter = painterResource(R.drawable.youtube_logo), // You'll need this
                        contentDescription = "YouTube",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .width(60.dp)
                    )

                    // Trailer title
                    Text(
                        text = trailer.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .fillMaxWidth(0.7f),
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black,
                                offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }
        }
    }
}