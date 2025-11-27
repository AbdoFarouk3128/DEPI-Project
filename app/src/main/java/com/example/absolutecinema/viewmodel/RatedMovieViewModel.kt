package com.example.absolutecinema.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.absolutecinema.ui.componants.RatingStatistics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

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