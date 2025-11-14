package com.example.absolutecinema.ui.componants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.absolutecinema.navigation.Screen

data class NavigationItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String,
    val requiresAuth: Boolean = false
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    onAuthRequired: () -> Unit
) {
    val items = listOf(
        NavigationItem(Screen.Explore, Icons.Default.Home, "Explore", requiresAuth = false),
        NavigationItem(Screen.Home, Icons.Default.Search, "Search", requiresAuth = false),
        NavigationItem(Screen.Lists, Icons.Default.List, "List", requiresAuth = true),
        NavigationItem(Screen.Profile, Icons.Default.Person, "Profile", requiresAuth = true)
    )

    NavigationBar(
        containerColor = Color.Black,
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Check if authentication is required
                    if (item.requiresAuth && !isUserLoggedIn) {
                        onAuthRequired()
                    } else {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label, fontWeight = FontWeight.Bold)},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Red,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.White // Color of the selected item indicator
                )

            )
        }
    }
}