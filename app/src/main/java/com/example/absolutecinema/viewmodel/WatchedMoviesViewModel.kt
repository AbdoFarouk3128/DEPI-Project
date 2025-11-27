package com.example.absolutecinema.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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