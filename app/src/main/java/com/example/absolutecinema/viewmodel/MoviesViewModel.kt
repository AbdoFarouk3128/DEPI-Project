package com.example.absolutecinema.viewmodel

import Results
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// ✅ FIXED: Added default values for all parameters
data class WatchlistMovieData(
    val movieId: String = "",
    val posterPath: String = ""
)

// ✅ FIXED: Added default values
data class LikedMovieData(
    val movieId: String = "",
    val posterPath: String = ""
)

// ✅ FIXED: Added default values
data class WatchedMovieData(
    val movieId: String = "",
    val posterPath: String = ""
)

data class RatedMovieData(
    val movieId: String = "",
    var rating: Int = 0
)

class WatchlistMoviesViewModel : ViewModel() {

    private val _watchlist = MutableLiveData<MutableList<WatchlistMovieData>>(mutableListOf())
    val watchlist: LiveData<MutableList<WatchlistMovieData>> = _watchlist

    private val firestore = FirebaseFirestore.getInstance()
    // ✅ FIXED: Get userId dynamically to support multiple users
    private fun getUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    // ✅ FIXED: Get user-specific reference
    private fun getUserWatchlistRef() = firestore.collection("users").document(getUserId()).collection("watchlist")

//    init {
//        // Automatically load data when ViewModel is created
//        loadWatchlist()
//    }

    // 🟢 Add or remove movie locally + update Firestore
    fun watchlistControl(movieId: String, posterPath: String) {
        val currentList = _watchlist.value ?: mutableListOf()
        val existingItem = currentList.find { it.movieId == movieId }
        val userWatchlistRef = getUserWatchlistRef()

        if (existingItem != null) {
            // Remove locally and in Firestore
            currentList.remove(existingItem)
            userWatchlistRef.document(movieId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Watchlist", "Removed movie from Firestore: $movieId")
                }
                .addOnFailureListener { e ->
                    Log.e("Watchlist", "Error removing movie: $e")
                }
        } else {
            // Add locally and to Firestore
            val newMovie = WatchlistMovieData(movieId, posterPath)
            currentList.add(newMovie)

            userWatchlistRef.document(movieId)
                .set(newMovie)
                .addOnSuccessListener {
                    Log.d("Watchlist", "Added movie to Firestore: $movieId")
                }
                .addOnFailureListener { e ->
                    Log.e("Watchlist", "Error adding movie: $e")
                }
        }

        // Update LiveData
        _watchlist.value = currentList
    }

    // 🟣 Fetch movies from Firestore and update LiveData
    fun loadWatchlist() {
        val userWatchlistRef = getUserWatchlistRef()
        userWatchlistRef.get()
            .addOnSuccessListener { snapshot ->
                val fetchedMovies = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(WatchlistMovieData::class.java)
                }.toMutableList()

                _watchlist.value = fetchedMovies
                Log.d("Watchlist", "Loaded ${fetchedMovies.size} movies from Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("Watchlist", "Error loading watchlist: $e")
            }
    }

    // 🔁 Optional: Listen to live Firestore updates in real-time
    fun listenToWatchlistChanges() {
        val userWatchlistRef = getUserWatchlistRef()
        userWatchlistRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Watchlist", "Listen failed.", e)
                return@addSnapshotListener
            }

            val updatedList = snapshot?.documents?.mapNotNull {
                it.toObject(WatchlistMovieData::class.java)
            }?.toMutableList()

            if (updatedList != null) {
                _watchlist.value = updatedList
                Log.d("Watchlist", "Realtime update received: ${updatedList.size} movies")
            }
        }
    }
}

class LikedMoviesViewModel : ViewModel() {

    private val _likedList = MutableLiveData<MutableList<LikedMovieData>>(mutableListOf())
    val likedList: LiveData<MutableList<LikedMovieData>> = _likedList

    private val firestore = FirebaseFirestore.getInstance()
    // ✅ FIXED: Get userId dynamically to support multiple users
    private fun getUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    // ✅ FIXED: Get user-specific reference
    private fun getUserLikedRef() = firestore.collection("users").document(getUserId()).collection("liked")

    // Add or remove movie
    fun likedListControl(movieId: String, posterPath: String) {
        val currentList = _likedList.value ?: mutableListOf()
        val existingItem = currentList.find { it.movieId == movieId }
        val userLikedRef = getUserLikedRef()

        if (existingItem != null) {
            currentList.remove(existingItem)
            userLikedRef.document(movieId).delete()
                .addOnFailureListener { e ->
                    Log.e("LikedMovies", "Error removing movie: $e")
                }
        } else {
            val newMovie = LikedMovieData(movieId, posterPath)
            currentList.add(newMovie)
            userLikedRef.document(movieId)
                .set(newMovie)
                .addOnFailureListener { e ->
                    Log.e("LikedMovies", "Error adding movie: $e")
                }
        }
        _likedList.value = currentList
    }

    // Load from Firestore
    fun loadLikedMovies() {
        val userLikedRef = getUserLikedRef()
        userLikedRef.get()
            .addOnSuccessListener { snapshot ->
                val fetchedMovies = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(LikedMovieData::class.java)
                }.toMutableList()
                _likedList.value = fetchedMovies
            }
            .addOnFailureListener { e ->
                Log.e("LikedMovies", "Error loading: $e")
            }
    }
}

class WatchedMoviesViewModel : ViewModel() {

    private val _watchedList = MutableLiveData<MutableList<WatchedMovieData>>(mutableListOf())
    val watchedList: LiveData<MutableList<WatchedMovieData>> = _watchedList

    private val firestore = FirebaseFirestore.getInstance()
    // ✅ FIXED: Get userId dynamically to support multiple users
    private fun getUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    // ✅ FIXED: Get user-specific reference
    private fun getUserWatchedRef() = firestore.collection("users").document(getUserId()).collection("watched")

    fun watchedListControl(movieId: String, posterPath: String) {
        val currentList = _watchedList.value ?: mutableListOf()
        val existingItem = currentList.find { it.movieId == movieId }
        val userWatchedRef = getUserWatchedRef()

        if (existingItem != null) {
            currentList.remove(existingItem)
            userWatchedRef.document(movieId).delete()
                .addOnFailureListener { e ->
                    Log.e("WatchedMovies", "Error removing movie: $e")
                }
        } else {
            val newMovie = WatchedMovieData(movieId, posterPath)
            currentList.add(newMovie)
            userWatchedRef.document(movieId)
                .set(newMovie)
                .addOnFailureListener { e ->
                    Log.e("WatchedMovies", "Error adding movie: $e")
                }
        }
        _watchedList.value = currentList
    }

    fun loadWatchedMovies() {
        val userWatchedRef = getUserWatchedRef()
        userWatchedRef.get()
            .addOnSuccessListener { snapshot ->
                val fetchedMovies = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(WatchedMovieData::class.java)
                }.toMutableList()
                _watchedList.value = fetchedMovies
            }
            .addOnFailureListener { e ->
                Log.e("WatchedMovies", "Error loading: $e")
            }
    }
}

class RatedMovieViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _ratedMovies = MutableLiveData<MutableList<RatedMovieData>>(mutableListOf())
    val ratedMovies: LiveData<MutableList<RatedMovieData>> = _ratedMovies

    // ✅ FIXED: Get user-specific reference
    private fun getUserRatedRef() = auth.currentUser?.let { user ->
        firestore.collection("users").document(user.uid).collection("ratedMovies")
    }

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
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("RatedMovieViewModel", "User not authenticated")
            _currentRating.value = 0  // Default to 0 if not authenticated
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("ratedMovies")
            .document(movieId)
            .get()
            .addOnSuccessListener { document ->
                val rating = if (document.exists()) {
                    document.getLong("rating")?.toInt() ?: 0
                } else {
                    0  // No rating found for this movie
                }
                _currentRating.value = rating
                Log.d("RatedMovieViewModel", "Fetched rating for $movieId: $rating")
            }
            .addOnFailureListener { e ->
                Log.e("RatedMovieViewModel", "Error fetching rating for $movieId", e)
                _currentRating.value = 0  // Default to 0 on error
            }
    }
}