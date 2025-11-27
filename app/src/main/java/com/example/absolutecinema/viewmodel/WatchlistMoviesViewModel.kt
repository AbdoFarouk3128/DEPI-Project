package com.example.absolutecinema.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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