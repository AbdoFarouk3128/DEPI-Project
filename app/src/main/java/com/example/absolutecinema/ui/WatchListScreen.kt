package com.example.absolutecinema.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.absolutecinema.viewmodel.MoviesViewModel


@Composable
fun WatchlistScreen(viewModel: MoviesViewModel) {
    val watchlist by viewModel.watchlist.observeAsState(emptyList())
    if (watchlist.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Your watchlist is empty")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
        ) {
            items(watchlist.size) { movieId ->
                Text("Movie ID: $movieId", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
