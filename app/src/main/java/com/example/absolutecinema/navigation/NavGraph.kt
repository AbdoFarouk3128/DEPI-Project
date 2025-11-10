package com.example.absolutecinema.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.absolutecinema.ui.LikedListScreen
import com.example.absolutecinema.ui.MovieDetails
import com.example.absolutecinema.ui.MovieScreen
import com.example.absolutecinema.ui.WatchedScreen
import com.example.absolutecinema.ui.WatchlistScreen
import com.example.absolutecinema.viewmodel.LikedMoviesViewModel
import com.example.absolutecinema.viewmodel.RatedMovieViewModel
import com.example.absolutecinema.viewmodel.WatchedMoviesViewModel
import com.example.absolutecinema.viewmodel.WatchlistMoviesViewModel
import com.google.gson.Gson
import java.net.URLDecoder

@Composable
fun NavGraph(
    navController: NavHostController,
    watchlistViewModel: WatchlistMoviesViewModel,
    likedMoviesViewModel: LikedMoviesViewModel,
    watchedListViewModel:WatchedMoviesViewModel,
    ratedMovieViewModel: RatedMovieViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Movies.route
    ) {
        //from here
        composable(route = Screen.Movies.route) {
            MovieScreen(
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                },

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
                watchedMoviesViewModel =  watchedListViewModel,
                ratedMovieViewModel = ratedMovieViewModel,
                onBack = { navController.popBackStack() },
                watchlistControl = { movieId, posterPath ->
                    watchlistViewModel.watchlistControl(movieId, posterPath)
                },
                likedListControl = { movieId, posterPath ->
                    likedMoviesViewModel.likedListControl(movieId, posterPath)

                },
                watchedListControl = {movieId, posterPath ->
                    watchedListViewModel.watchedListControl(movieId,posterPath)
                },
                ratedListControl = {movieId,rating->
                    ratedMovieViewModel.ratedMovies(movieId,rating)
                },
                gotoWatchlist = { movieId, poster ->
                    navController.navigate(Screen.Watchlist.createRoute(movieId, poster))
                },
                gotoLikedList = { movieId, poster ->
                    navController.navigate(Screen.LikedList.createRoute(movieId, poster))
                },
                gotoWatchedList = { movieId, poster ->
                    navController.navigate(Screen.Watched.createRoute(deliverables))
                }
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
            WatchedScreen (
                viewModel = watchedListViewModel,
                onMovieClick = { deliverables ->
                    navController.navigate(Screen.Details.createRoute(deliverables))
                }
            )
        }

    }
}
