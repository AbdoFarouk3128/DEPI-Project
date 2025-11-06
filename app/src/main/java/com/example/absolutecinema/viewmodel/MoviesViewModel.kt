package com.example.absolutecinema.viewmodel

import Cinema
import Results
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MoviesViewModel : ViewModel() {
    private val _movies = MutableLiveData<List<Results>>(emptyList())
    val movies: LiveData<List<Results>> = _movies

    private val _watchlist = MutableLiveData<MutableList<String>>(mutableListOf())
    val watchlist: LiveData<MutableList<String>> = _watchlist

    // Add/remove movie from watchlist
    fun watchlistControl(movieId: String) {
        val currentList = _watchlist.value ?: mutableListOf()
        if (currentList.contains(movieId)) {
            currentList.remove(movieId)

        } else {
            currentList.add(movieId)
        }
        _watchlist.value = currentList
        toggleWatched(movieId)
    }

    // Mark movie as watched/unwatched
    fun toggleWatched(movieId: String) {
        _movies.value = _movies.value?.map { movie ->
            if (movie.id == movieId) {
                movie.copy(isWatched = !movie.isWatched)
            } else movie
        }
    }
}
