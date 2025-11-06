package com.example.absolutecinema.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MovieDetails(
    movieId: String,
    onBack: () -> Unit,
    watchlistControl: (String) -> Unit,
    gotoWatchlist:()->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Movie Details for ID: $movieId")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
        Button(onClick = { watchlistControl(movieId) }) {
            Text("add to watchlist")
        }
        Button(onClick =  gotoWatchlist ) {
            Text("go to watchlist")
        }
    }
}