package com.example.mathq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.mathq.screens.* // Ensure all your screens are imported
import com.example.mathq.ui.theme.MathQTheme
import com.google.firebase.database.FirebaseDatabase
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathQTheme {
                val navController = rememberNavController()
                // Start the app at the login screen
                NavHost(navController = navController, startDestination = "login") {

                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                            onNavigateToLogin = { navController.navigate("login") }
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { userId ->
                                val db = FirebaseDatabase.getInstance().getReference("User")
                                db.child(userId).get().addOnSuccessListener { snapshot ->
                                    val name = snapshot.child("name").getValue(String::class.java) ?: "User"
                                    val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                                    // After login, go to home and clear the back stack
                                    navController.navigate("home/$userId/$encodedName") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onNavigateToRegister = { navController.navigate("register") }
                        )
                    }

                    // In MainActivity.kt, inside the NavHost composable
                    composable("home/{userId}/{userName}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                        HomeScreen(
                            userId = userId,
                            userName = userName,
                            navController = navController,
                            onEasyClick = { navController.navigate("easygame/$userId") },
                            onNormalClick = { navController.navigate("normalgame/$userId") },
                            onHardClick = { navController.navigate("hardgame/$userId") },
                            // This is the crucial part that was fixed:
                            onProfileClick = { currentUserId, currentUserName -> // <-- Lambda now accepts parameters
                                navController.navigate("profile/$currentUserId/$currentUserName")
                            }
                            // If you have Leaderboard/Notifications in HomeScreen, they also need this pattern:
                            // onLeaderboardClick = { currentUserId, currentUserName -> navController.navigate("leaderboard/$currentUserId/$currentUserName") },
                            // onNotificationsClick = { currentUserId, currentUserName -> navController.navigate("notifications/$currentUserId/$currentUserName") }
                        )
                    }

                    // --- FIX START: Profile route now expects userName ---
                    composable("profile/{userId}/{userName}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                        ProfileScreen(
                            userId = userId,
                            userName = userName, // Pass the extracted userName
                            navController = navController,
                            onNavigateToUpdate = { navController.navigate("update/$it") },
                            onNavigateToDelete = {
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        )
                    }
                    // --- FIX END ---

                    // You would add UpdateProfileScreen and DeleteProfileScreen composables here...
                    // Ensure update route also includes userName if needed by UpdateProfileScreen
                    // composable("update/{userId}/{userName}") { ... }

                    composable("easygame/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        EasyGameScreen(userId = userId, navController = navController)
                    }
                    composable("normalgame/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        NormalGameScreen(userId = userId, navController = navController)
                    }
                    composable("hardgame/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        HardGameScreen(userId = userId, navController = navController)
                    }


                    composable("leaderboard/{userId}/{userName}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                        LeaderboardScreen(
                            navController = navController,
                            userId = userId,
                            userName = userName,
                            onBackClick = {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("updatedUserName", userName)
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("notifications/{userId}/{userName}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                        NotificationScreen(
                            navController = navController,
                            userId = userId,
                            userName = userName,
                            onBackClick = {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("updatedUserName", userName)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}