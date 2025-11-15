package com.example.absolutecinema.navigation

import Results
import java.net.URLEncoder

sealed class Screen(val route: String) {

    object Home : Screen("moviesHome")
    object Topic : Screen("movies/{index}"){
        fun createRoute(index:Int): String {
            return "movies/$index"
        }
    }
    object UserLists:Screen("userList/{listType}"){
        fun createRoute(listType:String):String{
            return "userList/$listType"
        }
    }

    object Details : Screen("details/{deliverables}") {
        fun createRoute(deliverables: Deliverables): String {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(deliverables)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "details/$encoded"
        }
    }
    object SignUP : Screen("signup")
    object Login : Screen("login")
//    object Home : Screen("home")
    object Lists : Screen("lists")
    object Profile : Screen("profile")
    object Explore : Screen("explore")
}
data class Deliverables(
    val movieId: String,
    val poster: String,
    val isWatched:Boolean=false,
    val title: String=""
)
