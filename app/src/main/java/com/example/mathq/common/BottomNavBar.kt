package com.example.mathq.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // ✅ FIX: Added this crucial import for the 'by' delegate
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController, currentUserId: String) {
    // This now works because of the new import
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    ) {
        NavigationBarItem(
            selected = currentRoute == "profile/{userId}",
            onClick = { navController.navigate("profile/$currentUserId") },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "leaderboard/{userId}",
            onClick = { navController.navigate("leaderboard/$currentUserId") },
            icon = { Icon(Icons.Filled.EmojiEvents, contentDescription = "Leaderboard") },
            label = { Text("Leaderboard") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "notifications/{userId}",
            onClick = { navController.navigate("notifications/$currentUserId") },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
            label = { Text("Notifications") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )
    }
}
