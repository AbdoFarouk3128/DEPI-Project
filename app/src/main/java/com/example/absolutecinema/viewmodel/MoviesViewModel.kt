package com.example.absolutecinema.viewmodel

import Results
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    var rating: Int
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
class RatedMovieViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _ratedMovies = MutableLiveData<MutableList<RatedMovieData>>(mutableListOf())
    val ratedMovies: LiveData<MutableList<RatedMovieData>> = _ratedMovies

    // --- Add or update rating locally + in Firestore ---
    fun ratedMoviesControl(movieId: String, rating: Int) {
        val currentList = _ratedMovies.value ?: mutableListOf()
        val existingItem = currentList.find { it.movieId == movieId }

        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        val ratedMoviesRef = firestore.collection("users")
            .document(userId)
            .collection("ratedMovies")

        if (rating == 0) {
            // If rating is 0, remove this movie both locally and in Firestore
            if (existingItem != null) {
                currentList.remove(existingItem)
                ratedMoviesRef.document(movieId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("RatedMovieViewModel", "Deleted movie with rating 0: $movieId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("RatedMovieViewModel", "Error deleting movie", e)
                    }
            }
        } else {
            // If movie exists, update its rating
            if (existingItem != null) {
                existingItem.rating = rating
                ratedMoviesRef.document(movieId)
                    .update("rating", rating)
                    .addOnSuccessListener {
                        Log.d("RatedMovieViewModel", "Updated rating for $movieId to $rating")
                    }
                    .addOnFailureListener { e ->
                        Log.e("RatedMovieViewModel", "Error updating rating", e)
                    }
            } else {
                // Add new movie locally and remotely
                val newMovie = RatedMovieData(movieId, rating)
                currentList.add(newMovie)

                val movieData = hashMapOf(
                    "movieId" to movieId,
                    "rating" to rating
                )

                ratedMoviesRef.document(movieId)
                    .set(movieData)
                    .addOnSuccessListener {
                        Log.d("RatedMovieViewModel", "Added movie $movieId with rating $rating")
                    }
                    .addOnFailureListener { e ->
                        Log.e("RatedMovieViewModel", "Error adding movie", e)
                    }
            }
        }

        _ratedMovies.value = currentList
    }
    fun updateCurrentMovieRating(newRating: Int) {
        _currentRating.value = newRating
    }
    // --- Fetch all rated movies from Firestore ---
    fun fetchRatedMovies() {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .collection("ratedMovies")
            .get()
            .addOnSuccessListener { snapshot ->
                val movies = snapshot.documents.mapNotNull { doc ->
                    val movieId = doc.getString("movieId")
                    val rating = doc.getLong("rating")?.toInt()
                    if (movieId != null && rating != null) {
                        RatedMovieData(movieId, rating)
                    } else null
                }.toMutableList()

                _ratedMovies.value = movies
            }
            .addOnFailureListener { e ->
                Log.e("RatedMovieViewModel", "Error fetching rated movies", e)
            }
    }
    // Inside RatedMovieViewModel
    private val _currentRating = MutableLiveData<Int>()
    val currentRating: LiveData<Int> = _currentRating

    fun fetchMovieRating(movieId: String) {
        val currentUser = auth.currentUser ?: return
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("ratedMovies")
            .document(movieId)
            .get()
            .addOnSuccessListener { document ->
                val rating = document.getLong("rating")?.toInt() ?: 0
                _currentRating.value = rating
            }
    }
}
