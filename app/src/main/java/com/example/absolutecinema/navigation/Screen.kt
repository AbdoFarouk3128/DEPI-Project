package com.example.absolutecinema.navigation

sealed class Screen(val route: String) {
    object Movies : Screen("movies")
    object Details : Screen("details/{movieId}") {
        fun createRoute(movieId: String) = "details/$movieId"
    }
}
