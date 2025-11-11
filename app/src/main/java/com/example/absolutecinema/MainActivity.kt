package com.example.absolutecinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.absolutecinema.navigation.NavGraph
import com.example.absolutecinema.ui.componants.BottomNavigationBar
import com.example.absolutecinema.ui.theme.AbsoluteCinemaTheme
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.Login
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val startDestination = if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            "home"
        } else {
            "login"
        }
        setContent {
            AbsoluteCinemaTheme {
                val navController = rememberNavController()
                val watchListViewModel: WatchlistMoviesViewModel = viewModel()
                val likedListViewModel: LikedMoviesViewModel = viewModel()
                val watchedListViewModel: WatchedMoviesViewModel = viewModel()
                val ratedMovieViewModel: RatedMovieViewModel = viewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            watchlistViewModel = watchListViewModel,
                            likedMoviesViewModel = likedListViewModel,
                            watchedListViewModel = watchedListViewModel,
                            ratedMovieViewModel = ratedMovieViewModel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Navigation(auth: FirebaseAuth, startDestination: String, modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("signup") { SignUP(navController, auth) }
        composable("login") { Login(navController, auth) }
        composable("home") { Home(navController, auth) }
    }
}




