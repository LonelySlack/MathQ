package com.example.mathq

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// @OptIn(ExperimentalMaterial3Api::class) is often needed when using AlertDialog from Material3.
// Make sure you have this import as well if it's not already there:
// import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onResetPassword: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column {
                Text("Enter your email address to receive a password reset link.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        onResetPassword(email)
                    } else {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Send Reset Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}