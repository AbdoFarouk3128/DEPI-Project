package com.example.absolutecinema.navigation

import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Movies : Screen("movies")
    object Details : Screen("details/{deliverables}") {
        fun createRoute(deliverables: Deliverables): String {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(deliverables)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "details/$encoded"
        }
    }
    object Watchlist : Screen("watchlist/{movieId}/{posterPath}"){
        fun createRoute(movieId: String,poster:String) = "watchlist/${movieId}/${poster.removePrefix("/")}"
    }
    object LikedList : Screen("likedList/{movieId}/{posterPath}"){
        fun createRoute(movieId: String,poster:String) = "likedList/${movieId}/${poster.removePrefix("/")}"
    }
    object Watched:Screen("watched/{deliverables}"){
        fun createRoute(deliverables: Deliverables): String {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(deliverables)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "watched/$encoded"
        }
    }
}
data class Deliverables(
    val movieId: String,
    val poster: String,
    val isWatched:Boolean=false,
    val title: String=""
)
