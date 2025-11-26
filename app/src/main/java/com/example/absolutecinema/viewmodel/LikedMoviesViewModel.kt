package com.example.absolutecinema.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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