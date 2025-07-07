package com.example.mathq.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
// Removed unused import: import java.util.jar.Attributes.Name

@Composable
fun BottomNavBar(navController: NavController, currentUserId: String, userName: String) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    ) {

        // Home Item
        NavigationBarItem(
            // ✅ FIX: Use startsWith for home too for robustness, though exact match might work here if home is always the base.
            selected = currentRoute?.startsWith("home") == true,
            // ✅ FIX: Removed the trailing space from $userName
            onClick = {
                if (currentRoute != "home/$currentUserId/$userName") { // Avoid re-navigating if already on Home
                    navController.navigate("home/$currentUserId/$userName") {
                        // For Home, often you want to pop all other routes and go back to a clean Home state
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )

        // Profile Item
        NavigationBarItem(
            // ✅ FIX: Use startsWith to correctly check if the route corresponds to profile
            selected = currentRoute?.startsWith("profile") == true,
            // ✅ FIX: Pass userName in the navigation string
            onClick = {
                navController.navigate("profile/$currentUserId/$userName") {
                    // Optional: Improve navigation behavior for tab-like navigation
                    popUpTo("home/{userId}/{userName}") { saveState = true } // Pop up to home and save home's state
                    launchSingleTop = true // Avoid multiple copies of the same destination
                    restoreState = true // Restore state when re-selecting a previously selected tab
                }
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )

        // Leaderboard Item
        NavigationBarItem(
            // ✅ FIX: Use startsWith
            selected = currentRoute?.startsWith("leaderboard") == true,
            // ✅ FIX: Pass userName in the navigation string
            onClick = {
                navController.navigate("leaderboard/$currentUserId/$userName") {
                    popUpTo("home/{userId}/{userName}") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Filled.EmojiEvents, contentDescription = "Leaderboard") },
            label = { Text("Leaderboard") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )

        // Notifications Item
        NavigationBarItem(
            // ✅ FIX: Use startsWith
            selected = currentRoute?.startsWith("notifications") == true,
            // ✅ FIX: Pass userName in the navigation string
            onClick = {
                navController.navigate("notifications/$currentUserId/$userName") {
                    popUpTo("home/{userId}/{userName}") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
            label = { Text("Notifications") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )
    }
}