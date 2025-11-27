package com.example.absolutecinema.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.absolutecinema.data.helpers.shareMovie
import com.example.absolutecinema.data.onboarding.isOnBoardingComplete
import com.example.absolutecinema.ui.screens.ExploreScreen
import com.example.absolutecinema.ui.screens.HomeScreen
import com.example.absolutecinema.ui.screens.ListsScreen
import com.example.absolutecinema.ui.screens.Login
import com.example.absolutecinema.ui.screens.MovieDetails
import com.example.absolutecinema.ui.screens.OnBoardScreen
import com.example.absolutecinema.ui.screens.ProfileScreen
import com.example.absolutecinema.ui.screens.SignUP
import com.example.absolutecinema.ui.screens.SplashScreen
import com.example.absolutecinema.ui.screens.TopicScreen
import com.example.absolutecinema.ui.screens.UserListsScreen
import com.example.absolutecinema.viewmodel.FirebaseViewModel
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import androidx.core.content.edit

private lateinit var auth: FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedMoviesViewModel: LikedMoviesViewModel,
    watchedListViewModel: WatchedMoviesViewModel,
    ratedMovieViewModel: RatedMovieViewModel,
    firebaseViewModel: FirebaseViewModel,
    context: Context
) {
    auth = Firebase.auth
    val sharedPref = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

    val onboardingDone = sharedPref.getBoolean("onboarding_done", false)

    val startDestination = if (!onboardingDone) {
        Screen.OnBoard.route

    } else if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
        Screen.Explore.route
    }
    else{
        Screen.Login.route
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMovieClick = { deliverable ->
                    navController.navigate(Screen.Details.createRoute(deliverable))
                }
            )
        }
        //lists screen
        composable(route = Screen.Lists.route) { backStackEntry ->
            ListsScreen(
                watchlistViewModel = watchlistViewModel,
                likedMoviesViewModel = likedMoviesViewModel,
                watchedMoviesViewModel = watchedListViewModel,
                ratedMovieViewModel = ratedMovieViewModel,
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                },
                onSeeAllClick = { listType ->
                    navController.navigate(Screen.UserLists.createRoute(listType))
                }
            )
        }

        composable(Screen.OnBoard.route)
        {
            OnBoardScreen{
                sharedPref.edit { putBoolean("onboarding_done", true) }
                navController.navigate(Screen.Explore.route)
            }
        }
        composable(Screen.Splash.route) {
            SplashScreen {
                navController.navigate(startDestination) {
                    popUpTo(0)
                }
            }
        }



        composable(
            Screen.UserLists.route,
            arguments = listOf(navArgument("listType") {
                type = NavType.StringType
            })
        )
        { backStackEntry ->
            UserListsScreen(
                ratedMovieViewModel = ratedMovieViewModel,
                likedMoviesViewModel = likedMoviesViewModel,
                watchedMoviesViewModel = watchedListViewModel,
                watchlistMoviesViewModel = watchlistViewModel,
                listType = backStackEntry.arguments?.getString("listType") ?: "",
                goBack = {
                    navController.popBackStack()
                }
            ) { deliverables ->
                navController.navigate(Screen.Details.createRoute(deliverables))
            }

        }
        composable(route = Screen.Explore.route) {
            ExploreScreen(
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                },
                goToMovies = { index ->
                    navController.navigate(Screen.Topic.createRoute(index))
                },
                time = {

                },
                firebaseViewModel = firebaseViewModel
            )
        }
        //from here
        composable(
            route = Screen.Topic.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            TopicScreen(
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                },
                index = backStackEntry.arguments?.getInt("index") ?: 0,
                goBack = {
                    navController.popBackStack()
                }
            )
        }
        //to here

        //  Movie details screen
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("deliverables") { type = NavType.StringType },
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://movie-discovery-app-1fc65.web.app/{deliverables}"
                }
            )
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("deliverables") ?: ""
            val json = URLDecoder.decode(encodedJson, "UTF-8")
            val deliverables = Gson().fromJson(json, Deliverables::class.java)
            Log.d("del", "$deliverables")
            MovieDetails(
                movieId = deliverables.movieId,
                posterPath = deliverables.poster,
                watchlistViewModel = watchlistViewModel,
                likedListViewModel = likedMoviesViewModel,
                watchedMoviesViewModel = watchedListViewModel,
                ratedMovieViewModel = ratedMovieViewModel,
                onBack = { navController.popBackStack() },
                onMovieClick = { movieDeliverables ->
                    navController.navigate(Screen.Details.createRoute(movieDeliverables))
                },
                share = {
                    val encoded = URLEncoder.encode(json, "UTF-8")
                    val deepLink = "https://movie-discovery-app-1fc65.web.app/$encoded"
                    shareMovie(context, deepLink)
                }


            )
        }

        composable(route = Screen.SignUP.route) {
            SignUP(
                viewModel = firebaseViewModel,
                goToApp = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                haveAnAccount = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(route = Screen.Login.route) {
            Login(
                viewModel = firebaseViewModel,
                noAccount = {
                    navController.navigate(Screen.SignUP.route)
                },
                goToApp = {
                    navController.navigate(Screen.Explore.route) {
                        popUpTo(0)
                    }
                }
            )
        }

//        composable(route = Screen.Home.route) { Home(navController, auth) }
        // Profile Screen
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                viewModel = firebaseViewModel,
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}


