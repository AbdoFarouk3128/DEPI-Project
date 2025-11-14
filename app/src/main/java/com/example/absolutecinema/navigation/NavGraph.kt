package com.example.absolutecinema.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.absolutecinema.ui.ExploreScreen
import com.example.absolutecinema.ui.HomeScreen
import com.example.absolutecinema.ui.LikedListScreen
import com.example.absolutecinema.ui.ListsScreen
import com.example.absolutecinema.ui.Login
import com.example.absolutecinema.ui.MovieDetails
import com.example.absolutecinema.ui.ProfileScreen
import com.example.absolutecinema.ui.RatedScreen
import com.example.absolutecinema.ui.SignUP
import com.example.absolutecinema.ui.TopicScreen
import com.example.absolutecinema.ui.WatchedScreen
import com.example.absolutecinema.ui.WatchlistScreen
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

private lateinit var auth: FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedMoviesViewModel: LikedMoviesViewModel,
    watchedListViewModel: WatchedMoviesViewModel,
    ratedMovieViewModel: RatedMovieViewModel,
    firebaseViewModel: FirebaseViewModel
) {
    auth = Firebase.auth
    val startDestination = if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
        Screen.Explore.route
    } else {
        Screen.Login.route
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Home.route) {
        HomeScreen (
            onMovieClick = { deliverable ->
                navController.navigate(Screen.Details.createRoute(deliverable))
            }
        )
    }
        //lists screen
        composable(route = Screen.Lists.route) {
            ListsScreen(
                watchlistViewModel = watchlistViewModel,
                likedMoviesViewModel = likedMoviesViewModel,
                watchedMoviesViewModel = watchedListViewModel,
                ratedMovieViewModel = ratedMovieViewModel,
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                },
                onSeeAllClick = { listType ->
                    when (listType) {
                        "watchlist" -> navController.navigate(Screen.Watchlist.route)
                        "liked" -> navController.navigate(Screen.LikedList.route)
                        "watched" -> navController.navigate(Screen.Watched.route)
                        "rated" -> navController.navigate(Screen.Rated.route)
                    }
                }
            )
        }        //from here
        composable(route = Screen.Topic.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            TopicScreen(
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                },
                index = backStackEntry.arguments?.getInt("index")?:0,
                goBack = {
                    navController.popBackStack()
                }
            )
        }
        //to here

        //  Movie details screen
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("deliverables") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("deliverables") ?: ""
            val json = URLDecoder.decode(encodedJson, "UTF-8")
            val deliverables = Gson().fromJson(json, Deliverables::class.java)

            MovieDetails(
                movieId = deliverables.movieId,
                posterPath = deliverables.poster,
                title = deliverables.title,
                watchlistViewModel = watchlistViewModel,
                likedListViewModel = likedMoviesViewModel,
                watchedMoviesViewModel = watchedListViewModel,
                ratedMovieViewModel = ratedMovieViewModel,
                onBack = { navController.popBackStack() },
                onMovieClick = { movieDeliverables ->
                    navController.navigate(Screen.Details.createRoute(movieDeliverables))
                },
                watchlistControl = { movieId, posterPath ->
                    watchlistViewModel.watchlistControl(movieId, posterPath)
                },
                likedListControl = { movieId, posterPath ->
                    likedMoviesViewModel.likedListControl(movieId, posterPath)

                },
                watchedListControl = { movieId, posterPath ->
                    watchedListViewModel.watchedListControl(movieId, posterPath)
                },
                ratedListControl = { movieId, rating ->
                    ratedMovieViewModel.ratedMoviesControl(movieId, rating)
                },
//                gotoWatchlist = { movieId, poster ->
//                    navController.navigate(Screen.Watchlist.createRoute(movieId, poster))
//                },
//                gotoLikedList = { movieId, poster ->
//                    navController.navigate(Screen.LikedList.createRoute(movieId, poster))
//                },
//                gotoWatchedList = { movieId, poster ->
//                    navController.navigate(Screen.Watched.createRoute(deliverables))
//                }
            )
        }
        composable(
            route = Screen.Watchlist.route,
        ) { backStackEntry ->
            WatchlistScreen(
                viewModel = watchlistViewModel,
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                }
            )
        }
        composable(
            route = Screen.LikedList.route,
        ) { backStackEntry ->
            LikedListScreen(
                viewModel = likedMoviesViewModel,
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                }
            )
        }

        composable(
            route = Screen.Watched.route,
        ) { backStackEntry ->
            WatchedScreen(
                viewModel = watchedListViewModel,
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                }
            )
        }
        composable(
            route = Screen.Rated.route,
        ) { backStackEntry ->
            RatedScreen(
                viewModel = ratedMovieViewModel,
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
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
        composable(route = Screen.Explore.route) {
           ExploreScreen(
               onMovieClick =  { deliverables ->
                   navController.navigate(Screen.Details.createRoute(deliverables))
               },
               goToMovies = {index->
                   navController.navigate(Screen.Topic.createRoute(index))
               },
               firebaseViewModel = firebaseViewModel
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


