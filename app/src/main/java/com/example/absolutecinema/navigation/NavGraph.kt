package com.example.absolutecinema.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.absolutecinema.ui.MovieDetails
import com.example.absolutecinema.ui.MovieScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Movies.route
    ) {
        composable(route = Screen.Movies.route) {
            MovieScreen(
                modifier = Modifier,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Details.createRoute(movieId))
                }
            )
        }

        // 📝 Movie details screen
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            MovieDetails(
                movieId = movieId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
