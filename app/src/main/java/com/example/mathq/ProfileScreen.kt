package com.example.mathq.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mathq.common.AppBackground
import com.example.mathq.common.BottomNavBar
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    navController: NavController,
    onNavigateToUpdate: (String) -> Unit,
    onNavigateToDelete: (String) -> Unit
) {
    var name by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var highestEasy by remember { mutableStateOf(0) }
    var highestNormal by remember { mutableStateOf(0) }
    var highestHard by remember { mutableStateOf(0) }
    var createdAt by remember { mutableStateOf("Loading...") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            val ref = FirebaseDatabase.getInstance().getReference("User").child(userId)
            ref.get().addOnSuccessListener {
                name = it.child("name").getValue(String::class.java) ?: "N/A"
                email = it.child("email").getValue(String::class.java) ?: "N/A"
                highestEasy = it.child("highest_easy_score").getValue(Int::class.java) ?: 0
                highestNormal = it.child("highest_normal_score").getValue(Int::class.java) ?: 0
                highestHard = it.child("highest_hard_score").getValue(Int::class.java) ?: 0
                createdAt = it.child("created_at").getValue(String::class.java)?.substringBefore(" ") ?: "N/A"
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, currentUserId = userId) }
    ) { paddingValues ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("User Profile", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        ProfileItem(label = "Name", value = name)
                        Divider()
                        ProfileItem(label = "Email", value = email)
                        Divider()
                        ProfileItem(label = "Highest Easy Score", value = highestEasy.toString())
                        Divider()
                        ProfileItem(label = "Highest Normal Score", value = highestNormal.toString())
                        Divider()
                        ProfileItem(label = "Highest Hard Score", value = highestHard.toString())
                        Divider()
                        ProfileItem(label = "Joined On", value = createdAt)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onNavigateToUpdate(userId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Update Profile", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Profile", fontSize = 16.sp)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Profile?") },
            text = { Text("Are you sure? All your data will be permanently lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onNavigateToDelete(userId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ✅ This composable now lives ONLY in this file, which removes the conflict.
@Composable
fun ProfileItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}
