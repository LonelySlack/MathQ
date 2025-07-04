package com.example.mathq.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mathq.common.AppBackground
import com.example.mathq.data.AuthRepository

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // ✅ UX FIX: State to track loading

    // ✅ UI FIX: Applying the background image to the entire screen.
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground // ✅ UI FIX: Using theme color
            )
            Spacer(modifier = Modifier.height(24.dp))

            // ✅ UI FIX: Making TextFields fill the width for a cleaner look
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true // ✅ UX FIX: Show loading indicator
                        // ✅ LOGIC FIX: Calling the organized AuthRepository
                        AuthRepository.registerUser(name, email, password) { result ->
                            isLoading = false // ✅ UX FIX: Hide loading indicator
                            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                            if (result == "Registered successfully!") {
                                onRegisterSuccess()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                enabled = !isLoading, // ✅ UX FIX: Disable button while loading
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // ✅ UI FIX: Use yellow from theme
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                // ✅ UX FIX: Show a loading circle or the text based on isLoading state
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateToLogin, enabled = !isLoading) {
                Text(
                    text = "Already have an account? Login",
                    color = MaterialTheme.colorScheme.onBackground // ✅ UI FIX: Use theme color
                )
            }
        }
    }
}
