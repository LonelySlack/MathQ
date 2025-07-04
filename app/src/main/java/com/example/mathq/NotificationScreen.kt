package com.example.mathq.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.mathq.common.AppBackground
import com.example.mathq.common.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    userId: String,
    onBackClick: () -> Unit
) {
    // ✅ FIX: The function now correctly accepts the parameters from MainActivity.
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, currentUserId = userId) }
    ) { paddingValues ->
        AppBackground {
            // Your UI for notifications will go here.
            // This is just a placeholder.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Notifications Screen")
            }
        }
    }
}
