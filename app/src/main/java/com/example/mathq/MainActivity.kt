package com.example.mathq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.mathq.screens.*
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

                    composable("home/{userId}/{userName}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val userName = backStackEntry.arguments?.getString("userName") ?: "User"
                        HomeScreen(
                            userId = userId,
                            userName = userName,
                            navController = navController,
                            onEasyClick = { navController.navigate("easygame/$userId") },
                            onNormalClick = { navController.navigate("normalgame/$userId") },
                            onHardClick = { navController.navigate("hardgame/$userId") }
                        )
                    }

                    composable("profile/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        ProfileScreen(
                            userId = userId,
                            navController = navController,
                            onNavigateToUpdate = { navController.navigate("update/$it") },
                            onNavigateToDelete = {
                                // After deleting, go back to login screen and clear all previous screens
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        )
                    }

                    // You would add UpdateProfileScreen and DeleteProfileScreen composables here...

                    composable("easygame/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        EasyGameScreen(userId = userId, navController = navController)
                    }
                    // Define normalgame and hardgame routes here...

                    // ✅ FIX: The routes for leaderboard and notifications now correctly accept the userId
                    composable("leaderboard/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        LeaderboardScreen(
                            navController = navController,
                            userId = userId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("notifications/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        // This is a placeholder screen, you would create your actual UI here
                        NotificationScreen(
                            navController = navController,
                            userId = userId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
