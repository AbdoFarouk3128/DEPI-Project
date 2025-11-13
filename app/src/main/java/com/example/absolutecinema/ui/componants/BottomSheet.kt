package com.example.absolutecinema.ui.componants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absolutecinema.R
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    movieId: String,
    posterPath: String,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedListViewModel: LikedMoviesViewModel,
    watchedMoviesViewModel: WatchedMoviesViewModel,
    ratedMovieViewModel: RatedMovieViewModel,
    onDismiss: () -> Unit,
) {
    // ✅ Observe LiveData from ViewModels
    val watchlist by watchlistViewModel.watchlist.observeAsState(mutableListOf())
    val likedList by likedListViewModel.likedList.observeAsState(mutableListOf())
    val watchedList by watchedMoviesViewModel.watchedList.observeAsState(mutableListOf())
    val currentRating by ratedMovieViewModel.currentRating.observeAsState(0)

    // ✅ Check if movie exists in each list
    var isWatched by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var isWatch by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    // ✅ Fetch the movie rating when component loads
    LaunchedEffect(movieId) {
        ratedMovieViewModel.fetchMovieRating(movieId)
    }

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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Watchlist Button - Call ViewModel method directly
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(
                            if (isWatched) R.drawable.watchlist_added else R.drawable.watchlist_not
                        ),
                        contentDescription = "Watchlist Icon",
                        modifier = Modifier
                            .clickable {
                                isWatched = !isWatched
                                // ✅ Call ViewModel method
                                watchlistViewModel.watchlistControl(movieId, posterPath)
                            }
                            .size(120.dp),
                        tint = if (isWatched) Color.Yellow else Color.Gray
                    )
                    Text("WatchList", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }

                // ✅ Liked Button - Call ViewModel method directly
                Column  (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        painter = painterResource(
                            if (isLiked) R.drawable.heart else R.drawable.heart_empty
                        ),
                        contentDescription = "Liked Icon",
                        modifier = Modifier
                            .clickable {
                                isLiked = !isLiked
                                // ✅ Call ViewModel method
                                likedListViewModel.likedListControl(movieId, posterPath)
                            }
                            .size(120.dp),
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                    Text(if(isLiked)"Liked" else "Like", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }

                // ✅ Watched Button - Call ViewModel method directly
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        painter = painterResource(
                            if (isWatch) R.drawable.watched else R.drawable.not_watched
                        ),
                        contentDescription = "Watched Icon",
                        modifier = Modifier
                            .clickable {
                                isWatch = !isWatch
                                // ✅ Call ViewModel method
                                watchedMoviesViewModel.watchedListControl(movieId, posterPath)
                            }
                            .size(120.dp),
                        tint = if (isWatch) Color.Green else Color.Gray
                    )
                    Text(if(isWatch)"Watched" else "Watch", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }
                RatingBar(
                    modifier = Modifier
                        .size(70.dp)
                        .padding(top = 16.dp),
                    rating = currentRating,
                    onRatingChanged = { newRating ->
                        ratedMovieViewModel.updateCurrentMovieRating(newRating)
                        ratedMovieViewModel.ratedMoviesControl(movieId, newRating)
                    },
                    movieId = movieId,
                    starsColor = Color.Red,
                    saveRating = { id, newRating ->
                        ratedMovieViewModel.ratedMoviesControl(id, newRating)
                    }
                )
            Text("Rating", fontWeight = FontWeight.SemiBold)



        }
    }
}