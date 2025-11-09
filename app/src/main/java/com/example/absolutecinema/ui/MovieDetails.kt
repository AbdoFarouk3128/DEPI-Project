package com.example.absolutecinema.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.absolutecinema.R
import com.example.absolutecinema.ui.componants.RatingBar
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MovieDetails(
    movieId: String,
    posterPath:String,
    title:String,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedListViewModel: LikedMoviesViewModel,
    onBack: () -> Unit,
    watchlistControl: (String,String) -> Unit,
    likedListControl: (String,String) -> Unit,
    gotoWatchlist:(String,String)->Unit,
    gotoLikedList:(String,String)->Unit
) {
    val watchlist by watchlistViewModel.watchlist.observeAsState(mutableListOf())
    val likedList by likedListViewModel.likedList.observeAsState(mutableListOf())

    val initialWatched = watchlist.any { it.movieId == movieId }
    val initialLiked = likedList.any { it.movieId == movieId }

    var isWatched by remember { mutableStateOf(initialWatched) }
    var isLiked by remember { mutableStateOf(initialLiked) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var rating1 by remember { mutableIntStateOf(0) }
    LaunchedEffect(watchlist) {
        isWatched = watchlist.any { it.movieId == movieId }

    }
    LaunchedEffect(likedList) {
        isLiked = likedList.any { it.movieId == movieId }

    }
    GlideImage(
        model = "https://image.tmdb.org/t/p/w500/${posterPath}",
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(top = 50.dp)
            .padding(horizontal = 50.dp)
            .size(150.dp)
            .clip(RoundedCornerShape(12.dp))

    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(text = "Movie Name : $title")
        Text(text = "Movie Details for ID: $movieId")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }

        Button(onClick = { gotoWatchlist(movieId, posterPath) } ) {
            Text("go to watchlist")
        }
        Button(onClick = { gotoLikedList(movieId, posterPath) } ) {
            Text("go to liked list")
        }
        Button(onClick = { gotoLikedList(movieId, posterPath) } ) {
            Text("go to watched list")
        }
        Button(onClick = {
            showBottomSheet = true
        } ) {
            Text("Manage Movie")
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Row {
                        Icon(
                            painter =painterResource(if (isWatched) R.drawable.watchlist_added else R.drawable.watchlist_not),
                            contentDescription = "watchlist Icon",
                            modifier = Modifier
                                .clickable {
                                    isWatched = !isWatched
                                    watchlistControl(movieId, posterPath)

                                }
                                .size(120.dp)
                        )
                        Icon(
                            painter = painterResource(if (isLiked) R.drawable.heart else R.drawable.heart_empty),
                            contentDescription = "Liked Icon",
                            modifier = Modifier
                                .clickable {
                                    isLiked = !isLiked
                                    likedListControl(movieId, posterPath)
                                }
                                .size(120.dp)
                        )
                        Icon(
                            painter = painterResource(if (isLiked) R.drawable.watched else R.drawable.not_watched),
                            contentDescription = "watched Icon",
                            modifier = Modifier
                                .clickable {
                                    isLiked = !isLiked
                                    likedListControl(movieId, posterPath)
                                }
                                .size(120.dp)
                        )

                    }
                    RatingBar(
                        modifier = Modifier
                            .size(50.dp),
                        rating = rating1,
                        onRatingChanged = {
                            rating1 = it
                        },
                        starsColor = Color.Yellow,
                        onDoubleTap = {
                            rating1= it-1
                        }
                    )
                }

            }
        }

    }
}