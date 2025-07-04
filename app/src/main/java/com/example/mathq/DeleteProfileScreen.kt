package com.example.mathq.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

@Composable
fun DeleteProfileScreen(userId: String, onBackToLogin: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Are you sure you want to delete your profile?")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val ref = FirebaseDatabase.getInstance().getReference("User").child(userId)
                ref.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show()
                        onBackToLogin() // Go back to login after deletion
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Delete Profile")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Cancel")
        }
    }
}
