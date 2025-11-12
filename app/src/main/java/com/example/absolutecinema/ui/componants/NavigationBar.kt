package com.example.absolutecinema.ui.componants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.absolutecinema.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Triple(Screen.Home, Icons.Default.Home, "Movies"),
        Triple(Screen.Lists, Icons.Default.List, "Lists"), // ✅ NEW middle item
        Triple(Screen.Explore, Icons.Default.Favorite, "Explore")
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { (screen, icon, label) ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Avoid multiple copies of the same destination
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) }
            )
        }
    }
}