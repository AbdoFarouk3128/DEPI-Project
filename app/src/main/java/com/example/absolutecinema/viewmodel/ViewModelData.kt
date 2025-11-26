package com.example.absolutecinema.viewmodel

data class WatchedMovieData(
    val movieId: String = "",
    val posterPath: String = ""
)

data class RatedMovieData(
    val movieId: String = "",
    var rating: Int = 0
)
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
data class UserProfile(
    val firstName: String = "",
    val secondName: String = "",
    val email: String = "",
    val birthday: String = "",
    val profileImageUrl: String = ""
)
