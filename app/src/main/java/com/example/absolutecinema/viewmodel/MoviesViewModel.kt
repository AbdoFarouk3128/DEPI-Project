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
data class WatchedMovieData(
    val movieId: String,
    val posterPath: String
)
data class RatedMovieData(
    val movieId: String,
    val rating: Int
)
class WatchlistMoviesViewModel : ViewModel() {

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
class WatchedMoviesViewModel : ViewModel() {

    private val _watchedList = MutableLiveData<MutableList<WatchedMovieData>>(mutableListOf())
    val watchedList: LiveData<MutableList<WatchedMovieData>> = _watchedList

    // Add or remove movie from watchlist
    fun watchedListControl(movieId: String, posterPath: String) {
        val currentList = _watchedList.value ?: mutableListOf()

        val existingItem = currentList.find { it.movieId == movieId }

        if (existingItem != null) {
            // Remove it if already in watchlist
            currentList.remove(existingItem)
        } else {
            // Add new movie with its poster
            currentList.add(WatchedMovieData(movieId, posterPath))
        }
        _watchedList.value = currentList

    }

}
class RatedMovieViewModel:ViewModel(){
    private val _ratedMovies =MutableLiveData<MutableList<RatedMovieData>>(mutableListOf())
    val ratedMovies :LiveData<MutableList<RatedMovieData>> =_ratedMovies

    fun ratedMovies(movieId: String,rating: Int){
        val currentList =_ratedMovies.value?: mutableListOf()
        val existingItem = currentList.find { it.movieId==movieId }
        if (existingItem != null) {
            // Remove it if already in watchlist
            currentList.remove(existingItem)
        } else {
            // Add new movie with its poster
            currentList.add(RatedMovieData(movieId, rating))
        }
        _ratedMovies.value = currentList
    }
}
