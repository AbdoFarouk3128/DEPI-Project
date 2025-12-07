package com.example.absolutecinema.ui.screens


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.R
import com.example.absolutecinema.data.api.Results
import com.example.absolutecinema.data.api.getDetails
import com.example.absolutecinema.navigation.Deliverables
import com.example.absolutecinema.ui.componants.RatingBar
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

    var refresh by remember { mutableStateOf(false) }

    // ✅ Check if movie exists in each list


    LaunchedEffect(listType,refresh) {
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

        isLoading = false
    }
if(refresh)
    {  // FULL SCREEN BACKGROUND
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

                        val currentUserRating by ratedMovieViewModel.currentUserRating.observeAsState(
                            0
                        )
                        ratedMovieViewModel.fetchUserRating(movie.id)
                        var posterPosition by remember { mutableStateOf(Offset.Zero) }
                        var posterSize by remember { mutableStateOf(IntSize.Zero) }
                        var popup by remember { mutableStateOf(false) }
                        var popupSize by remember { mutableStateOf(IntSize.Zero) }

                        var isWatched by remember { mutableStateOf(false) }
                        var isLiked by remember { mutableStateOf(false) }
                        var isWatch by remember { mutableStateOf(false) }

                        val scale by animateFloatAsState(
                            targetValue = if (popup) 1f else 0.7f,
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = FastOutSlowInEasing
                            )
                        )
                        // ✅ Update local states when LiveData changes
                        LaunchedEffect(watchedList) {
                            isWatched = watchlistList.any { it.movieId == movie.id }
                        }

                        LaunchedEffect(likedList) {
                            isLiked = likedList.any { it.movieId == movie.id }
                        }

                        LaunchedEffect(watchedList) {
                            isWatch = watchedList.any { it.movieId == movie.id }
                        }

                        GlideImage(
                            model = "https://image.tmdb.org/t/p/w500/${movie.poster}",
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.7f)
                                .clip(RoundedCornerShape(12.dp))
                                .onGloballyPositioned { layout ->
                                    posterPosition = layout.localToRoot(Offset.Zero)
                                    posterSize = layout.size
                                }
                                .combinedClickable(
                                    onLongClick = {
                                        popup = true

                                    },
                                    onClick = {
                                        val deliverable = Deliverables(
                                            movieId = movie.id,
                                            poster = movie.poster,
                                            title = movie.title
                                        )
                                        onMovieClick(deliverable)
                                    })

                        )
                        if (popup) {
                            Popup(
                                offset = IntOffset(
                                    x = (posterPosition.x + posterSize.width / 2 - popupSize.width / 2).toInt(),
                                    y = (posterPosition.y - popupSize.height - 10).toInt()
                                ),
                                onDismissRequest = {
                                    popup = false
                                },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .onGloballyPositioned { popupSize = it.size }
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                            shadowElevation = 14f
                                        }
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .width((posterSize.width * 0.55f).dp) // SMALLER than poster
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {

                                        PopupOption(
                                            controlMovie = {
                                                isWatched = !isWatched
                                                // ✅ Call ViewModel method
                                                watchlistMoviesViewModel.watchlistControl(
                                                    movie.id,
                                                    movie.poster
                                                )
                                                popup = false
                                                refresh = !refresh
                                            },
                                            text = "Watchlist",
                                            icon = if (isWatched) R.drawable.watchlist_added else R.drawable.watchlist_not
                                        )
                                        DividerLine()

                                        PopupOption(
                                            controlMovie = {
                                                isLiked = !isLiked
                                                // ✅ Call ViewModel method
                                                likedMoviesViewModel.likedListControl(
                                                    movie.id,
                                                    movie.poster
                                                )
                                                popup = false
                                                refresh = !refresh
                                            },
                                            text = "Liked Movies",
                                            icon = if (isLiked) R.drawable.heart else R.drawable.heart_empty
                                        )
                                        DividerLine()

                                        PopupOption(
                                            controlMovie = {
                                                isWatch = !isWatch
                                                // ✅ Call ViewModel method
                                                watchedMoviesViewModel.watchedListControl(
                                                    movie.id,
                                                    movie.poster
                                                )
                                                popup = false
                                                refresh = !refresh
                                            },
                                            text = "Watched List",
                                            icon = if (isWatch) R.drawable.watched else R.drawable.not_watched
                                        )
                                        DividerLine()

                                        RatingBar(
                                            modifier = Modifier
                                                .size(35.dp)
                                                .padding(top = 16.dp),
                                            rating = currentUserRating,
                                            onRatingChanged = { newRating ->
                                                ratedMovieViewModel.updateCurrentMovieRating(
                                                    newRating
                                                )
                                                if(newRating==0)
                                                {
                                                    refresh=!refresh
                                                }
                                            },
                                            movieId = movie.id,
                                            starsColor = Color.Red,
                                            saveRating = { id, newRating ->
                                                ratedMovieViewModel.ratedMoviesControl(
                                                    id,
                                                    newRating
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                    }
                }

            }

        }
    }
    else{
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

                        val currentUserRating by ratedMovieViewModel.currentUserRating.observeAsState(
                            0
                        )
                        ratedMovieViewModel.fetchUserRating(movie.id)
                        var posterPosition by remember { mutableStateOf(Offset.Zero) }
                        var posterSize by remember { mutableStateOf(IntSize.Zero) }
                        var popup by remember { mutableStateOf(false) }
                        var popupSize by remember { mutableStateOf(IntSize.Zero) }

                        var isWatched by remember { mutableStateOf(false) }
                        var isLiked by remember { mutableStateOf(false) }
                        var isWatch by remember { mutableStateOf(false) }

                        val scale by animateFloatAsState(
                            targetValue = if (popup) 1f else 0.7f,
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = FastOutSlowInEasing
                            )
                        )
                        // ✅ Update local states when LiveData changes
                        LaunchedEffect(watchedList) {
                            isWatched = watchlistList.any { it.movieId == movie.id }
                        }

                        LaunchedEffect(likedList) {
                            isLiked = likedList.any { it.movieId == movie.id }
                        }

                        LaunchedEffect(watchedList) {
                            isWatch = watchedList.any { it.movieId == movie.id }
                        }

                        GlideImage(
                            model = "https://image.tmdb.org/t/p/w500/${movie.poster}",
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.7f)
                                .clip(RoundedCornerShape(12.dp))
                                .onGloballyPositioned { layout ->
                                    posterPosition = layout.localToRoot(Offset.Zero)
                                    posterSize = layout.size
                                }
                                .combinedClickable(
                                    onLongClick = {
                                        popup = true

                                    },
                                    onClick = {
                                        val deliverable = Deliverables(
                                            movieId = movie.id,
                                            poster = movie.poster,
                                            title = movie.title
                                        )
                                        onMovieClick(deliverable)
                                    })

                        )
                        if (popup) {
                            Popup(
                                offset = IntOffset(
                                    x = (posterPosition.x + posterSize.width / 2 - popupSize.width / 2).toInt(),
                                    y = (posterPosition.y - popupSize.height - 10).toInt()
                                ),
                                onDismissRequest = {
                                    popup = false
                                },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .onGloballyPositioned { popupSize = it.size }
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                            shadowElevation = 14f
                                        }
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .width((posterSize.width * 0.55f).dp) // SMALLER than poster
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {

                                        PopupOption(
                                            controlMovie = {
                                                isWatched = !isWatched
                                                // ✅ Call ViewModel method
                                                watchlistMoviesViewModel.watchlistControl(
                                                    movie.id,
                                                    movie.poster
                                                )
                                                popup = false
                                                refresh = !refresh
                                            },
                                            text ="Watchlist",
                                            icon = if (isWatched) R.drawable.watchlist_added else R.drawable.watchlist_not
                                        )
                                        DividerLine()

                                        PopupOption(
                                            controlMovie = {
                                                isLiked = !isLiked
                                                // ✅ Call ViewModel method
                                                likedMoviesViewModel.likedListControl(
                                                    movie.id,
                                                    movie.poster
                                                )
                                                popup = false
                                                refresh = !refresh
                                            },
                                            text =  "Liked Movies",
                                            icon = if (isLiked) R.drawable.heart else R.drawable.heart_empty
                                        )
                                        DividerLine()

                                        PopupOption(
                                            controlMovie = {
                                                isWatch = !isWatch
                                                // ✅ Call ViewModel method
                                                watchedMoviesViewModel.watchedListControl(
                                                    movie.id,
                                                    movie.poster
                                                )
                                                popup = false
                                                refresh = !refresh
                                            },
                                            text = "Watched Movies",
                                            icon = if (isWatch) R.drawable.watched else R.drawable.not_watched
                                        )
                                        DividerLine()

                                        RatingBar(
                                            modifier = Modifier
                                                .size(35.dp)
                                                .padding(top = 16.dp),
                                            rating = currentUserRating,
                                            onRatingChanged = { newRating ->
                                                ratedMovieViewModel.updateCurrentMovieRating(
                                                    newRating
                                                )
                                                if(newRating==0)
                                                {
                                                    refresh=!refresh
                                                }
                                            },
                                            movieId = movie.id,
                                            starsColor = Color.Red,
                                            saveRating = { id, newRating ->
                                                ratedMovieViewModel.ratedMoviesControl(
                                                    id,
                                                    newRating
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                    }
                }

            }

        }
    }

}

@Composable
fun PopupOption(controlMovie: () -> Unit, text: String, icon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { controlMovie() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 12.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DividerLine() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        thickness = 1.dp,
        color = Color(0xFFDDDDDD)
    )
}
