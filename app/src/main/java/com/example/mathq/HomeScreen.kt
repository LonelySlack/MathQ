package com.example.mathq.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mathq.common.AppBackground
import com.example.mathq.common.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String,
    userName: String,
    navController: NavController,
    onEasyClick: () -> Unit,
    onNormalClick: () -> Unit,
    onHardClick: () -> Unit
) {
    // ✅ FIX 1: Using Scaffold to properly place the BottomNavBar
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, currentUserId = userId) }
    ) { paddingValues ->
        // ✅ FIX 2: Applying the themed background
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Important: apply padding from the Scaffold
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Center the content vertically
            ) {
                Text(
                    text = "Welcome, $userName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(64.dp))

                // ✅ FIX 3: Calling the redesigned DifficultyButton
                DifficultyButton(title = "Easy", subtitle = "No Timer", onClick = onEasyClick)
                Spacer(modifier = Modifier.height(24.dp))
                DifficultyButton(title = "Normal", subtitle = "With Timer", onClick = onNormalClick)
                Spacer(modifier = Modifier.height(24.dp))
                DifficultyButton(title = "Hard", subtitle = "With Timer", onClick = onHardClick)
            }
        }
    }
}

@Composable
fun DifficultyButton(title: String, subtitle: String, onClick: () -> Unit) {
    // ✅ FIX 4: Styling the button to match your design mockup
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface, // White background from theme
            contentColor = MaterialTheme.colorScheme.onSurface // Dark text color from theme
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Softer color for subtitle
            )
        }
    }
}
