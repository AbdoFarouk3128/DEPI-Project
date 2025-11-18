package com.example.absolutecinema.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.absolutecinema.ui.componants.RatingStatistics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

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
    private fun getUserWatchlistRef() =
        firestore.collection("users").document(getUserId()).collection("watchlist")

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
                _watchlist.value = updatedList!!
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
    private fun getUserLikedRef() =
        firestore.collection("users").document(getUserId()).collection("liked")

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
    private fun getUserWatchedRef() =
        firestore.collection("users").document(getUserId()).collection("watched")

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


// RatedMovieViewModel.kt
class RatedMovieViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _ratingStatistics = MutableLiveData<RatingStatistics?>(null)
    val ratingStatistics: LiveData<RatingStatistics?> = _ratingStatistics


    private val _currentUserRating = MutableLiveData(0)
    val currentUserRating: LiveData<Int> = _currentUserRating

    private val _ratedMovies = MutableLiveData<List<RatedMovieData>>(emptyList())
    val ratedMovies: LiveData<List<RatedMovieData>> = _ratedMovies
    private var statisticsListener: ListenerRegistration? = null

    // Fetch ALL ratings for a specific movie (from ALL users)
    fun fetchRatingStatistics(movieId: String) {
        Log.d("RatingStats", "Setting up listener for movie: $movieId")

        // ⭐ Remove old listener first
        statisticsListener?.remove()

        // ⭐ Store the new listener
        statisticsListener = firestore.collectionGroup("ratedMovies")
            .whereEqualTo("movieId", movieId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RatingStats", "Error fetching ratings: ${error.message}")
                    return@addSnapshotListener
                }

                Log.d("RatingStats", "Snapshot received! Documents: ${snapshot?.size()}")

                val ratings = snapshot?.documents?.mapNotNull { doc ->
                    val rating = doc.getLong("rating")?.toInt()
                    Log.d(
                        "RatingStats",
                        "Doc: ${doc.id}, rating: $rating, movieId: ${doc.getString("movieId")}"
                    )
                    rating
                } ?: emptyList()

                Log.d("RatingStats", "Total ratings: ${ratings.size}, values: $ratings")
                calculateStatistics(ratings)
            }
    }

    private fun calculateStatistics(ratings: List<Int>) {
        if (ratings.isEmpty()) {
            _ratingStatistics.value = RatingStatistics()
            return
        }

        val distribution = (1..5).associateWith { star ->
            ratings.count { it == star }
        }

        val average = ratings.average().toFloat()

        _ratingStatistics.value = RatingStatistics(
            averageRating = average,
            totalRatings = ratings.size,
            ratingDistribution = distribution
        )
    }

    // Fetch current user's rating for a specific movie
    fun fetchUserRating(movieId: String) {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("ratedMovies")
            .document(movieId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rating = document.getLong("rating")?.toInt() ?: 0
                    _currentUserRating.value = rating
                } else {
                    _currentUserRating.value = 0
                }
            }
            .addOnFailureListener { e ->
                Log.e("Rating", "Error fetching user rating", e)
                _currentUserRating.value = 0
            }
    }

    // Save or update user's rating
    fun saveUserRating(movieId: String, rating: Int) {
        val userId = auth.currentUser?.uid ?: return

        val ratingData = hashMapOf(
            "movieId" to movieId,
            "rating" to rating,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userId)
            .collection("ratedMovies")
            .document(movieId)
            .set(ratingData)
            .addOnSuccessListener {
                _currentUserRating.value = rating
                Log.d("Rating", "Rating saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Rating", "Error saving rating", e)
            }
    }

    // Delete a rating
    fun deleteUserRating(movieId: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("ratedMovies")
            .document(movieId)
            .delete()
            .addOnSuccessListener {
                _currentUserRating.value = 0
                Log.d("Rating", "Rating deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Rating", "Error deleting rating", e)
            }
    }


    // Alternative method if you already have this
    fun ratedMoviesControl(movieId: String, rating: Int) {
        if (rating > 0) {
            saveUserRating(movieId, rating)
        } else {
            deleteUserRating(movieId)
        }
    }

    fun updateCurrentMovieRating(rating: Int) {
        _currentUserRating.value = rating
    }

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


}