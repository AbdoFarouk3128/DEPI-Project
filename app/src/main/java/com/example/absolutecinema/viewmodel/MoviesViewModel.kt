package com.example.absolutecinema.viewmodel

import Results
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class WatchlistMovieData(
    val movieId: String,
    val posterPath: String
)
data class LikedMovieData(
    val movieId: String,
    val posterPath: String
)
class WatchlistMoviesViewModel : ViewModel() {
     val watchlistMovies = MutableLiveData<List<Results>>(emptyList())

    private val _watchlist = MutableLiveData<MutableList<WatchlistMovieData>>(mutableListOf())
    val watchlist: LiveData<MutableList<WatchlistMovieData>> = _watchlist

    // Add or remove movie from watchlist
    fun watchlistControl(movieId: String, posterPath: String) {
        val currentList = _watchlist.value ?: mutableListOf()

        val existingItem = currentList.find { it.movieId == movieId }

        if (existingItem != null) {
            // Remove it if already in watchlist
            currentList.remove(existingItem)
        } else {
            // Add new movie with its poster
            currentList.add(WatchlistMovieData(movieId, posterPath))
        }
        _watchlist.value = currentList
    }


}
class LikedMoviesViewModel : ViewModel() {
    val moviesLiked = MutableLiveData<List<Results>>(emptyList())

    private val _likedList = MutableLiveData<MutableList<LikedMovieData>>(mutableListOf())
    val likedList: LiveData<MutableList<LikedMovieData>> = _likedList

    // Add or remove movie from watchlist
    fun likedListControl(movieId: String, posterPath: String) {
        val currentList = _likedList.value ?: mutableListOf()

        val existingItem = currentList.find { it.movieId == movieId }

        if (existingItem != null) {
            // Remove it if already in watchlist
            currentList.remove(existingItem)
        } else {
            // Add new movie with its poster
            currentList.add(LikedMovieData(movieId, posterPath))
        }
        _likedList.value = currentList

    }

}
