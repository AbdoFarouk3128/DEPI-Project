package com.example.absolutecinema.ui.componants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.absolutecinema.R
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMovie(
    modifier: Modifier,
    movieId: String,
    posterPath: String,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedListViewModel: LikedMoviesViewModel,
    watchedMoviesViewModel: WatchedMoviesViewModel,
) {
// ✅ Observe LiveData from ViewModels
    val watchlist by watchlistViewModel.watchlist.observeAsState(mutableListOf())
    val likedList by likedListViewModel.likedList.observeAsState(mutableListOf())
    val watchedList by watchedMoviesViewModel.watchedList.observeAsState(mutableListOf())

    // ✅ Check if movie exists in each list
    var isWatched by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var isWatch by remember { mutableStateOf(false) }




    // ✅ Update local states when LiveData changes
    LaunchedEffect(watchlist) {
        isWatched = watchlist.any { it.movieId == movieId }
    }

    LaunchedEffect(likedList) {
        isLiked = likedList.any { it.movieId == movieId }
    }

    LaunchedEffect(watchedList) {
        isWatch = watchedList.any { it.movieId == movieId }
    }

    Icon(
        painterResource( if (isWatched) R.drawable.watchlist_added else R.drawable.watchlist_not),
        contentDescription = "",
        modifier = Modifier.size(20.dp).clickable {
            isWatched = !isWatched
            // ✅ Call ViewModel method
            watchlistViewModel.watchlistControl(movieId, posterPath)
        },
        tint = Color.White
    )
    Spacer(Modifier.width(16.dp))
    Icon(
        painterResource( if (isLiked) R.drawable.heart else R.drawable.heart_empty),
        contentDescription = "",
        modifier = Modifier.size(20.dp).clickable {
            isLiked = !isLiked
            // ✅ Call ViewModel method
            likedListViewModel.likedListControl(movieId, posterPath)
        },
        tint = Color.White
    )
    Spacer(Modifier.width(16.dp))
    Icon(
        painterResource(if (isWatch) R.drawable.watched else R.drawable.not_watched),
        contentDescription = "",
        modifier = Modifier.size(20.dp).clickable {
            isWatch = !isWatch
            // ✅ Call ViewModel method
            watchedMoviesViewModel.watchedListControl(movieId, posterPath)
        },
        tint = Color.White
    )
    Spacer(Modifier.width(16.dp))
}