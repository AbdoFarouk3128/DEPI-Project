package com.example.absolutecinema

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.absolutecinema.data.helpers.createNotificationChannel
import com.example.absolutecinema.data.helpers.scheduleDailyNotification
import com.example.absolutecinema.navigation.NavGraph
import com.example.absolutecinema.ui.componants.BottomNavigationBar
import com.example.absolutecinema.ui.theme.AbsoluteCinemaTheme
import com.example.absolutecinema.viewmodel.FirebaseViewModel
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel


class MainActivity : ComponentActivity() {
    var isDialogShown= mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestHandler = handlePermissionRequest()
        createNotificationChannel(this)
        setContent {
            AbsoluteCinemaTheme {
                val navController = rememberNavController()
                val firebaseViewModel: FirebaseViewModel = viewModel()
                val watchListViewModel: WatchlistMoviesViewModel = viewModel()
                val likedListViewModel: LikedMoviesViewModel = viewModel()
                val watchedListViewModel: WatchedMoviesViewModel = viewModel()
                val ratedMovieViewModel: RatedMovieViewModel = viewModel()
                // ✅ Observe auth state from LiveData - automatically updates!
                val isUserLoggedIn by firebaseViewModel.isLoggedIn.observeAsState(initial = false)

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val dontShowBottomBar = when (currentRoute) {
                    "signup", "login","onboard","splash" -> true
                    else -> false
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    requestHandler.launch(Manifest.permission.POST_NOTIFICATIONS)
                else {
                    scheduleDailyNotification(this)
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
//                        if(!dontShowBottomBar){
                        AnimatedVisibility(visible = !dontShowBottomBar) {
                        BottomNavigationBar(
                            navController = navController,
                            isUserLoggedIn = isUserLoggedIn,
                            onAuthRequired = {
                                navController.navigate("login") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
//                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            watchlistViewModel = watchListViewModel,
                            likedMoviesViewModel = likedListViewModel,
                            watchedListViewModel = watchedListViewModel,
                            ratedMovieViewModel = ratedMovieViewModel,
                            firebaseViewModel = firebaseViewModel,
                            context = LocalContext.current
                        )
                    }
                }
            }
        }

    }
    fun handlePermissionRequest(): ActivityResultLauncher<String> {
        val handler = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                scheduleDailyNotification(this)
            } else {
                isDialogShown.value=true
            }
        }
        return handler
    }
}