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
    object Watchlist : Screen("watchlist/{deliverables}"){
        fun createRoute(deliverables: Deliverables): String {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(deliverables)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "watched/$encoded"
        }
    }
    object LikedList : Screen("likedList/{deliverables}"){
        fun createRoute(deliverables: Deliverables): String {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(deliverables)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "watched/$encoded"
        }
    }
    object Watched:Screen("watched/{deliverables}"){
        fun createRoute(deliverables: Deliverables): String {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(deliverables)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "watched/$encoded"
        }
    }
    object Rated:Screen("rated/{deliverables}"){
        fun createRoute(deliverables: Deliverables): String {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(deliverables)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "watched/$encoded"
        }
    }
    object SignUP : Screen("signup")
    object Login : Screen("login")
//    object Home : Screen("home")
    object Lists : Screen("lists")
}
data class Deliverables(
    val movieId: String,
    val poster: String,
    val isWatched:Boolean=false,
    val title: String=""
)
